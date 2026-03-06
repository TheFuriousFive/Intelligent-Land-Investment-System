package com.example.landapp.service;

import com.example.landapp.entity.LandAuthenticator;
import com.example.landapp.entity.LandListing;
import com.example.landapp.repository.LandAuthenticatorRepository;
import com.example.landapp.repository.LandListingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LandAuthenticatorService {

    @Autowired
    private LandAuthenticatorRepository authenticatorRepository;

    @Autowired
    private LandListingRepository landRepository;

    // 1. Register a new Authenticator
    @Transactional
    public LandAuthenticator registerAuthenticator(LandAuthenticator authenticator) {
        // Here you would normally hash the password before saving
        authenticator.setPasswordHash("HASHED_" + authenticator.getPasswordHash());
        return authenticatorRepository.save(authenticator);
    }

    // 2. The Core Method: Authenticate Land!
    @Transactional
    public void authenticateLand(Long listingId, Long authenticatorId, boolean isApproved, String comments) {

        // Find the authenticator
        LandAuthenticator authenticator = authenticatorRepository.findById(authenticatorId)
                .orElseThrow(() -> new RuntimeException("Authenticator not found"));

        // Find the land listing
        LandListing listing = landRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Land Listing not found"));

        // Update the listing's verification status
        // (You'll need to add a 'verificationStatus' and 'verifiedBy' field to your LandListing entity)
        if (isApproved) {
            // listing.setVerificationStatus("APPROVED");
            // listing.setVerifiedBy(authenticator);
        } else {
            // listing.setVerificationStatus("REJECTED");
        }

        // Save the updated listing
        landRepository.save(listing);
    }
}