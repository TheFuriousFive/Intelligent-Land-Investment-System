
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


    public void searchLandListings(String keyword, Double maxPrice) {
        // TO DO: Use landRepository to find matching lands
        // e.g., landRepository.findByTitleContainingAndPriceLessThan(...)
    }

    public void askQuestion(Long investorId, Long landListingId, String questionContent) {
        // TO DO: Create a Message entity with type "QUESTION"
        // Link it to the Investor (Sender) and the LandListing
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

