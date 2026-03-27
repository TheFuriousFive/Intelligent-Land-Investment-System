package com.example.landapp.service;

import com.example.landapp.dto.*;
import com.example.landapp.entity.*;
import com.example.landapp.mapper.LandListingMapper;
import com.example.landapp.mapper.OwnerMapper;
import com.example.landapp.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OwnerService {

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private OwnerMapper ownerMapper;

    @Autowired
    private LandListingRepository landRepository;

    @Autowired
    private LandListingMapper landMapper;

    @Autowired
    private TrustScoreService trustScoreService;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private MapDataGetting mapDataService;


    // 1. CREATE Listing
    // Add this injection at the top of OwnerService
    @Autowired
    private SupabaseStorageService supabaseStorageService;

    @Autowired
    private InquiryRepository inquiryRepository;

    @Transactional
    public void updateOwnerProfile(Long ownerId, OwnerUpdateDTO updateDto) {
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found with ID: " + ownerId));

        if (updateDto.firstName() != null) owner.setFirstName(updateDto.firstName());
        if (updateDto.lastName() != null) owner.setLastName(updateDto.lastName());
        if (updateDto.contactNumber() != null) owner.setContactNumber(updateDto.contactNumber());

        ownerRepository.save(owner);
    }

    // 1. CREATE Listing (Updated Signature)
    @Transactional
    public LandListingResponseDTO createListing(LandListingCreateDTO dto, List<MultipartFile> images, MultipartFile deedDocument) throws Exception {
        Owner owner = ownerRepository.findById(dto.getOwnerId())
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        LandListing listing = landMapper.toEntity(dto);
        listing.setOwner(owner);

        // --- NEW: UPLOAD FILES TO SUPABASE ---

        // 1. Upload Images
        List<String> imageUrls = new java.util.ArrayList<>();
        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                String url = supabaseStorageService.uploadFile(image);
                imageUrls.add(url);
            }
        }
        listing.setImageUrls(imageUrls);

        // 2. Upload Deed Document
        List<String> documentUrls = new java.util.ArrayList<>();
        if (deedDocument != null && !deedDocument.isEmpty()) {
            String deedUrl = supabaseStorageService.uploadFile(deedDocument);
            documentUrls.add(deedUrl);
        }
        listing.setDeedDocumentUrls(documentUrls);

        // --- END FILE UPLOADS ---

        MapDataGetting.MapDataResult mapResult = mapDataService.getLandContext(dto.getLatitude(), dto.getLongitude());
        landMapper.addMapDataToEntity(listing, mapResult);

        LandListing savedListing = landRepository.save(listing);

        return landMapper.toResponseDTO(savedListing);
    }

    // 2. DELETE Listing
    @Transactional
    public void deleteListing(Long listingId, Long ownerId) {

        LandListing listing = landRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        if (!listing.getOwner().getId().equals(ownerId)){
            throw new RuntimeException("Unauthorized: You can only delete your own listings");
        }

        landRepository.delete(listing);

    }

    // 3. UPDATE Listing
    @Transactional
    public void updateListing(Long listingId, LandListingCreateDTO updateDto) {
        LandListing existingListing = landRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        if (!existingListing.getOwner().getId().equals(updateDto.getOwnerId())){
            throw new RuntimeException("Unauthorized: You can only update your own listings");
        }

        // Update the existing entity with new values from the DTO
        landMapper.updateEntityFromDto(updateDto, existingListing);

        landRepository.save(existingListing);

    }

    public void answerQuestion(Long questionId, Long ownerId, String answerContent) {

        //Find the question — throw 404 if not found
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        // STEP 2: Find the owner who is answering
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        //Make sure this owner actually owns the listing
        if (!question.getLandListing().getOwner().getId().equals(ownerId)) {
            throw new RuntimeException("Unauthorized: You can only answer questions on your own listings");
        }

        // Build the Answer entity
        Answer answer = new Answer();
        answer.setQuestion(question);                  // FK → questionId
        answer.setOwner(owner);                        // FK → ownerId
        answer.setContent(answerContent);
        answer.setAnsweredAt(LocalDateTime.now());

        //Save to database
        answerRepository.save(answer);
    }

    public List<OwnerInboxResponseDTO> getOwnerInquiries(Long ownerId) {
        List<Inquiry> inquiries = inquiryRepository.findByOwnerIdOrderByCreatedAtDesc(ownerId);

        return inquiries.stream().map(inquiry -> {
            OwnerInboxResponseDTO dto = new OwnerInboxResponseDTO();
            dto.setInquiryId(inquiry.getId());
            dto.setLandListingId(inquiry.getLandListing().getId());
            dto.setLandTitle(inquiry.getLandListing().getTitle());
            dto.setCustomMessage(inquiry.getMessage());

            // FULL NAME Implementation
            dto.setInvestorFullName(inquiry.getInvestor().getFirstName() + " " + inquiry.getInvestor().getLastName());
            dto.setInvestorContactNumber(inquiry.getInvestor().getContactNumber());
            dto.setInvestorEmail(inquiry.getInvestor().getEmail());

            dto.setStatus(inquiry.getStatus());
            dto.setCreatedAt(inquiry.getCreatedAt());
            return dto;
        }).toList();
    }

    // 2. Owner replies to the inquiry
    @Transactional
    public void replyToInquiry(Long inquiryId, Long ownerId, ContactMethod method) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new RuntimeException("Inquiry not found"));

        if (!inquiry.getOwner().getId().equals(ownerId)) {
            throw new RuntimeException("Unauthorized: This inquiry does not belong to you.");
        }

        // Update the status and save the chosen contact method
        inquiry.setStatus(InquiryStatus.CONTACTED);
        inquiry.setChosenContactMethod(method);
        inquiryRepository.save(inquiry);
    }

    public List<LandListingResponseDTO> getOwnerListings(Long ownerId) {
        // 1. Fetch the raw entities from the database
        List<LandListing> listings = landRepository.findByOwnerId(ownerId);

        // 2. Convert them to Response DTOs and return the list
        return listings.stream()
                .map(landMapper::toResponseDTO)
                .toList();




    }

    public OwnerResponseDTO getOwnerById(Long ownerId) {

        trustScoreService.calculateTrustScore(ownerId);


        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found"));
        return ownerMapper.toResponseDTO(owner);
    }
}