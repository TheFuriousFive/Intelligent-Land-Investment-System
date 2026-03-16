//package com.example.landapp.service;
//
//import com.example.landapp.dto.InvestorRegistrationDTO;
//import com.example.landapp.dto.InvestorResponseDTO;
//import com.example.landapp.entity.Investor;
//import com.example.landapp.mapper.InvestorMapper;
//import com.example.landapp.repository.InvestorRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Service
//public class InvestorService {
//
//    @Autowired
//    private InvestorRepository investorRepository;
//
//    @Autowired
//    private InvestorMapper investorMapper;
//
//    // In the near future ... comment these studff to implement the methods given below.
//    // @Autowired private LandListingRepository landRepository;
//    // @Autowired private MessageRepository messageRepository;
//    // @Autowired private ReviewRepository reviewRepository;
//
//
//
//
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

import com.example.landapp.dto.InvestorRegistrationDTO;
import com.example.landapp.dto.InvestorResponseDTO;
import com.example.landapp.dto.LandListingResponseDTO;
import com.example.landapp.entity.*;
import com.example.landapp.mapper.InvestorMapper;
import com.example.landapp.mapper.LandListingMapper;
import com.example.landapp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvestorService {

    @Autowired
    private InvestorRepository investorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LandListingRepository landListingRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private InvestorMapper investorMapper;

    @Autowired
    private LandListingMapper landListingMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // REGISTER INVESTOR
    public InvestorResponseDTO registerInvestor(InvestorRegistrationDTO dto) {

        //Check if email is already taken
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already in use: " + dto.getEmail());
        }

        //Convert DTO → Entity using the mapper
        Investor investor = investorMapper.toEntity(dto);

        // the plain-text password before saving
        investor.setPassword(passwordEncoder.encode(dto.getPassword()));

        //Save the investor entity to the database
        Investor savedInvestor = investorRepository.save(investor);

        //Convert saved Entity → Response DTO
        return investorMapper.toResponseDTO(savedInvestor);
    }



    //SEARCH LAND LISTINGS
    public List<LandListingResponseDTO> searchLandListings(String keyword, Double maxPrice) {

        // all active listings from the database
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

        // Filter by maxPrice if provided
        if (maxPrice != null) {
            listings = listings.stream()
                    // doubleValue() converts BigDecimal price to a double for comparison
                    .filter(l -> l.getPrice().doubleValue() <= maxPrice)
                    .collect(Collectors.toList());
        }

        // STEP 4: Convert each LandListing entity → LandListingResponseDTO
        // .stream().map() applies landListingMapper.toResponseDTO()
        // to every listing in the list.
        // Result: a List of DTOs (safe for frontend) instead of List of Entities.
        return listings.stream()
                .map(landListingMapper::toResponseDTO)
                // landListingMapper::toResponseDTO is a method reference —
                // shorthand for: listing -> landListingMapper.toResponseDTO(listing)
                .collect(Collectors.toList());
    }


    // ═══════════════════════════════════════════════════════
    // METHOD 3 — ASK A QUESTION
    // Called by: InvestorController (POST /{investorId}/listings/{listingId}/questions)
    // Job: find investor + listing → create Question entity → save → done
    // ═══════════════════════════════════════════════════════
    public void askQuestion(Long investorId, Long listingId, String content) {

        // STEP 1: Find the investor from DB by their ID
        // findById() returns an Optional<Investor> — it might not exist.
        // .orElseThrow() — if not found, throw exception → 404 response.
        Investor investor = investorRepository.findById(investorId)
                .orElseThrow(() -> new RuntimeException("Investor not found: " + investorId));

        // STEP 2: Find the land listing from DB by its ID
        // Same pattern — throw if not found.
        LandListing listing = landListingRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found: " + listingId));

        // STEP 3: Build the Question entity
        // We create a new Question object and set all its fields.
        Question question = new Question();
        question.setContent(content);         // the actual question text
        question.setInvestor(investor);        // who asked it
        question.setLandListing(listing);      // which listing it belongs to
        question.setPostedDate(new Date());    // when it was asked

        // STEP 4: Save the question to the database
        // This inserts a new row in the question table.
        // We don't return anything — controller sends a success message.
        questionRepository.save(question);
    }


    // ═══════════════════════════════════════════════════════
    // METHOD 4 — SUBMIT A REVIEW
    // Called by: InvestorController (POST /{investorId}/listings/{listingId}/reviews)
    // Job: validate rating → create Review entity → save → update trust score idea
    // ═══════════════════════════════════════════════════════
    public void submitReview(Long investorId, Long listingId, int rating, String reviewText) {

        // STEP 1: Validate the rating range
        // Rating must be between 1 and 5.
        // If not, we reject it immediately before touching the DB.
        if (rating < 1 || rating > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }

        // STEP 2: Find investor — throw 404 if not found
        Investor investor = investorRepository.findById(investorId)
                .orElseThrow(() -> new RuntimeException("Investor not found: " + investorId));

        // STEP 3: Find listing — throw 404 if not found
        LandListing listing = landListingRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found: " + listingId));

        // STEP 4: Build the Review entity
        Review review = new Review();
        review.setRating(rating);              // numeric score e.g. 4
        review.setReviewText(reviewText);      // written feedback
        review.setInvestor(investor);          // who wrote it
        review.setLandListing(listing);        // which listing it's about
        review.setPostedDate(new Date());      // when it was submitted

        // STEP 5: Save the review
        // According to your state machine diagram, when a new Review
        // is received, the listing transitions to Calculating_Trust_Score.
        // That logic would go in a TrustScoreService in the future.
        reviewRepository.save(review);

        // FUTURE: trustScoreService.recalculate(listing);
        // This is where your Trust Score Calculator (from the project doc)
        // would be triggered — aggregating all reviews to update the score.
    }


    // ═══════════════════════════════════════════════════════
    // METHOD 5 — INQUIRE ABOUT LAND
    // Called by: InvestorController (POST /{investorId}/listings/{listingId}/inquiry)
    // Job: investor expresses intent to buy → send message to owner
    // This maps to the Inquiry_Sent state in your state machine diagram
    // ═══════════════════════════════════════════════════════
    public void inquireAboutLand(Long investorId, Long listingId) {

        // STEP 1: Find the investor
        Investor investor = investorRepository.findById(investorId)
                .orElseThrow(() -> new RuntimeException("Investor not found: " + investorId));

        // STEP 2: Find the listing
        LandListing listing = landListingRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found: " + listingId));

        // STEP 3: Get the owner from the listing
        // The LandListing entity already has the Owner linked via @ManyToOne.
        // No extra DB call needed — we just call listing.getOwner().
        Owner owner = listing.getOwner();

        // STEP 4: Build a Message entity
        // This represents the Inquiry — from investor to owner.
        // Your state machine shows: Inquiry_Sent → Contact_Established
        Message message = new Message();
        message.setSender(investor);           // investor is the sender
        message.setReceiver(owner);            // owner receives the inquiry
        message.setLandListing(listing);       // which listing this is about
        message.setContent(
                "Hello, I am interested in your listing: " + listing.getTitle()
                        + ". Please contact me to discuss further."
        );
        message.setSentAt(new Date());         // timestamp

        // STEP 5: Save the message
        // In a real production app you might also send an email notification
        // to the owner here using a NotificationService.
        messageRepository.save(message);
    }
}