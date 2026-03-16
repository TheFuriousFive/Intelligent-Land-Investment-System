package com.example.landapp.service;

import com.example.landapp.dto.InvestorRegistrationDTO;
import com.example.landapp.dto.InvestorResponseDTO;
import com.example.landapp.dto.LandListingResponseDTO;
import com.example.landapp.entity.Investor;
import com.example.landapp.entity.LandListing;
import com.example.landapp.entity.ListingStatus;
import com.example.landapp.entity.Question;
import com.example.landapp.mapper.InvestorMapper;
import com.example.landapp.mapper.LandListingMapper;
import com.example.landapp.repository.InvestorRepository;
import com.example.landapp.repository.LandListingRepository;
import com.example.landapp.repository.QuestionRepository;
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

//    @Autowired
//    private UserRepository userRepository;
//    // Talks to the base user table — used to check
//    // if an email is already registered (across ALL user types)

    @Autowired
    private LandListingRepository landListingRepository;

    @Autowired
    private QuestionRepository questionRepository;
    // Talks to the question table
    // Used to save a new Question entity

//    @Autowired
//    private ReviewRepository reviewRepository;
//    // Talks to the review table
//    // Used to save a new Review entity

//    @Autowired
//    private MessageRepository messageRepository;
//    // Talks to the message table
//    // Used to save an inquiry message from investor to owner

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
        question.setContent(content);         // the actual question text
        question.setInvestorId(investorId);        // who asked it
        question.setLandListing(listing);      // which listing it belongs to
        question.setCreatedAt(LocalDateTime.now());    // when it was asked

        //Save the question to the database
        questionRepository.save(question);
    }

    public void submitReview(Long investorId, Long landListingId, int rating, String reviewText) {
        // TO DO: Create a Review entity
        // Link it to the Investor and the LandListing
    }

    public void inquireAboutLand(Long investorId, Long landListingId) {
        // TO DO: This could send an automated email to the Owner
        // or create an "Inquiry" record in the database.
    }
}
