package com.example.landapp.service;

import com.example.landapp.dto.LandListingCreateDTO;
import com.example.landapp.dto.LandListingResponseDTO;
import com.example.landapp.dto.OwnerRegistrationDTO;
import com.example.landapp.dto.OwnerResponseDTO;
import com.example.landapp.entity.Answer;
import com.example.landapp.entity.LandListing;
import com.example.landapp.entity.Owner;
import com.example.landapp.entity.Question;
import com.example.landapp.mapper.LandListingMapper;
import com.example.landapp.mapper.OwnerMapper;
import com.example.landapp.repository.AnswerRepository;
import com.example.landapp.repository.LandListingRepository;
import com.example.landapp.repository.OwnerRepository;
import com.example.landapp.repository.QuestionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    @Transactional
    public LandListingResponseDTO createListing(LandListingCreateDTO dto) {
        Owner owner = ownerRepository.findById(dto.getOwnerId())
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        LandListing listing = landMapper.toEntity(dto);

        listing.setOwner(owner);

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