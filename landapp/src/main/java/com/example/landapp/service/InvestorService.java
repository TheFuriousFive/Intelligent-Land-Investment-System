
//    public void searchLandListings(String keyword, Double maxPrice) {
//        // TO DO: Use landRepository to find matching lands
//        // e.g., landRepository.findByTitleContainingAndPriceLessThan(...)
//    }
//
//    public void askQuestion(Long investorId, Long landListingId, String questionContent) {
//        // TO DO: Create a Message entity with type "QUESTION"
//        // Link it to the Investor (Sender) and the LandListing
//    }
//
//    public void submitReview(Long investorId, Long landListingId, int rating, String reviewText) {
//        // TO DO: Create a Review entity
//        // Link it to the Investor and the LandListing
//    }
//
//    public void inquireAboutLand(Long investorId, Long landListingId) {
//        // TO DO: This could send an automated email to the Owner
//        // or create an "Inquiry" record in the database.
//    }
//}
//------------------------------------------------------------------------------------------------
package com.example.landapp.service;

import com.example.landapp.dto.*;
import com.example.landapp.entity.*;
import com.example.landapp.mapper.InvestorMapper;
import com.example.landapp.mapper.LandListingMapper;
import com.example.landapp.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvestorService {

    @Autowired
    private InvestorRepository investorRepository;


    @Autowired
    private LandListingRepository landListingRepository;

    @Autowired
    private QuestionRepository questionRepository;


    @Autowired
    private ReviewRepository reviewRepository;


//    @Autowired
//    private MessageRepository messageRepository;
//    // Talks to the message table
//    // Used to save an inquiry message from investor to owner

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private UserRepository userRepository;



    @Autowired
    private InvestorMapper investorMapper;

    @Autowired
    private LandListingMapper landListingMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private InquiryRepository inquiryRepository;

    @Transactional
    public void updateInvestorProfile(Long investorId, InvestorUpdateDTO updateDto) {
        Investor investor = investorRepository.findById(investorId)
                .orElseThrow(() -> new RuntimeException("Investor not found with ID: " + investorId));

        // Base user fields
        if (updateDto.firstName() != null) investor.setFirstName(updateDto.firstName());
        if (updateDto.lastName() != null) investor.setLastName(updateDto.lastName());
        if (updateDto.contactNumber() != null) investor.setContactNumber(updateDto.contactNumber());

        // Investor specific fields
        if (updateDto.preferredLocation() != null) investor.setPreferredLocation(updateDto.preferredLocation());
        if (updateDto.investmentBudget() != null) investor.setInvestmentBudget(updateDto.investmentBudget());

        investorRepository.save(investor);
    }

    public List<LandListingResponseDTO> searchLandListings(String keyword, Double maxPrice) {

        //Fetch all active listings from the database
        List<LandListing> listings = landListingRepository
                .findByStatus(ListingStatus.AVAILABLE);

        //Filter by keyword if provided
        if (keyword != null && !keyword.isBlank()) {
            listings = listings.stream()
                    .filter(l ->
                            l.getTitle().toLowerCase().contains(keyword.toLowerCase())
                                    || l.getLocation().toLowerCase().contains(keyword.toLowerCase())
                    )
                    .collect(Collectors.toList());
        }

        //Filter by maxPrice if provided
        if (maxPrice != null) {
            listings = listings.stream()
                    // doubleValue() converts BigDecimal price to a double for comparison
                    .filter(l -> l.getPrice().doubleValue() <= maxPrice)
                    .collect(Collectors.toList());
        }

        //Convert each LandListing entity → LandListingResponseDTO
        return listings.stream()
                .map(landListingMapper::toResponseDTO)
                .collect(Collectors.toList());
    }


    public void askQuestion(Long investorId, Long listingId, String content) {

        //Find the investor from DB by their ID
        Investor investor = investorRepository.findById(investorId)
                .orElseThrow(() -> new RuntimeException("Investor not found: " + investorId));

        //Find the land listing from DB by its ID
        LandListing listing = landListingRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found: " + listingId));

        //Build the Question entity
        Question question = new Question();
        question.setContent(content);
        question.setInvestor(investor);
        question.setLandListing(listing);
        question.setCreatedAt(LocalDateTime.now());

        //Save the question to the database
        questionRepository.save(question);
    }

    public void submitReview(Long investorId,Long ownerId, int rating, String comment) {

        //Validate the rating range
        if (rating < 1 || rating > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }

        Investor investor = investorRepository.findById(investorId)
                .orElseThrow(() -> new RuntimeException("Investor not found: " + investorId));

        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found: "+ ownerId));

        //Build the Review entity
        Review review = new Review();
        review.setRating(rating);
        review.setComment(comment);
        review.setInvestor(investor);
        review.setOwner(owner);
        review.setCreatedAt(LocalDateTime.now());

        //Save the review
        reviewRepository.save(review);


    }

    // 1. Create the Inquiry (Simplified, no longer returns instant owner details)
    public void inquireAboutLand(Long investorId, Long listingId, String customMessage) {
        if (inquiryRepository.existsByInvestorIdAndLandListingId(investorId, listingId)) {
            throw new RuntimeException("You have already sent an inquiry for this property!");
        }

        Investor investor = investorRepository.findById(investorId).orElseThrow();
        LandListing listing = landListingRepository.findById(listingId).orElseThrow();

        Inquiry inquiry = new Inquiry();
        inquiry.setInvestor(investor);
        inquiry.setLandListing(listing);
        inquiry.setOwner(listing.getOwner());
        inquiry.setMessage(customMessage);
        inquiryRepository.save(inquiry);
    }

    // 2. Fetch Investor's History (Generates the dynamic failsafe messages!)
    public List<InvestorInquiryResponseDTO> getInvestorInquiries(Long investorId) {
        List<Inquiry> inquiries = inquiryRepository.findByInvestorIdOrderByCreatedAtDesc(investorId);

        return inquiries.stream().map(inquiry -> {
            InvestorInquiryResponseDTO dto = new InvestorInquiryResponseDTO();
            dto.setInquiryId(inquiry.getId());
            dto.setLandTitle(inquiry.getLandListing().getTitle());
            dto.setOwnerFullName(inquiry.getOwner().getFirstName() + " " + inquiry.getOwner().getLastName());
            dto.setStatus(inquiry.getStatus());
            dto.setCreatedAt(inquiry.getCreatedAt());

            // THE MAGIC: Generate the contextual message for the Next.js UI
            if (inquiry.getStatus() == InquiryStatus.PENDING) {
                dto.setPlatformUpdateMessage("Your inquiry has been sent! Waiting for the owner to review it.");
            } else if (inquiry.getStatus() == InquiryStatus.CONTACTED) {
                String methodStr = inquiry.getChosenContactMethod() == ContactMethod.EMAIL ? "an email" : "a phone call";
                String ownerPhone = inquiry.getOwner().getContactNumber();

                dto.setPlatformUpdateMessage(
                        "The owner will send " + methodStr + " to contact you. " +
                                "If the owner doesn't contact you within the relevant time period, please contact them directly at: " + ownerPhone
                );
            }

            return dto;
        }).toList();
    }
    public InvestorResponseDTO getInvestorById(Long investorId) {




        Investor investor = investorRepository.findById(investorId)
                .orElseThrow(() -> new RuntimeException("Investor not found"));
        return investorMapper.toResponseDTO(investor);
    }

}

