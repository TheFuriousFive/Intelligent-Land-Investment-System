
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

    public void inquireAboutLand(Long investorId, Long listingId) {

//        //Find the investor
//        Investor investor = investorRepository.findById(investorId)
//                .orElseThrow(() -> new RuntimeException("Investor not found: " + investorId));
//
//        //Find the listing
//        LandListing listing = landListingRepository.findById(listingId)
//                .orElseThrow(() -> new RuntimeException("Listing not found: " + listingId));
//
//        //Get the owner from the listing
//        Owner owner = listing.getOwner();
//
//        //Build a Message entity
//        Message message = new Message();
//        message.setSender(investor);
//        message.setReceiver(owner);
//        message.setLandListing(listing);
//        message.setContent(
//                "Hello, I am interested in your listing: " + listing.getTitle()
//                        + ". Please contact me to discuss further."
//        );
//        message.setSentAt(new Date());         // timestamp
//
//        //Save the message
//        messageRepository.save(message);
    }

}

