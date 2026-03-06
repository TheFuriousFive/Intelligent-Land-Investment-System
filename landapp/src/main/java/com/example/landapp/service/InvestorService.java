package com.example.landapp.service;

import com.example.landapp.dto.InvestorRegistrationDTO;
import com.example.landapp.dto.InvestorResponseDTO;
import com.example.landapp.entity.Investor;
import com.example.landapp.mapper.InvestorMapper;
import com.example.landapp.repository.InvestorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvestorService {

    @Autowired
    private InvestorRepository investorRepository;

    @Autowired
    private InvestorMapper investorMapper;

    // In the near future ... comment these studff to implement the methods given below.
    // @Autowired private LandListingRepository landRepository;
    // @Autowired private MessageRepository messageRepository;
    // @Autowired private ReviewRepository reviewRepository;


    public InvestorResponseDTO registerInvestor(InvestorRegistrationDTO registrationDTO) {
        Investor investor = investorMapper.toEntity(registrationDTO);

        // Fake hashing for now ! To be implemented later
        investor.setPasswordHash("SAFE_HASH_" + registrationDTO.getPassword());

        Investor savedInvestor = investorRepository.save(investor);
        return investorMapper.toResponseDTO(savedInvestor);
    }


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
