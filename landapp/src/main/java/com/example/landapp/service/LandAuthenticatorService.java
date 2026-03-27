package com.example.landapp.service;

import com.example.landapp.dto.AuthenticatorUpdateDTO;
import com.example.landapp.entity.LandAuthenticator;
import com.example.landapp.entity.LandListing;
import com.example.landapp.entity.VerificationStatus;
import com.example.landapp.repository.LandAuthenticatorRepository;
import com.example.landapp.repository.LandListingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

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

    @Transactional
    public void updateAuthenticatorProfile(Long authenticatorId, AuthenticatorUpdateDTO updateDto) {
        // 1. Find the exact authenticator in the database
        LandAuthenticator authenticator = authenticatorRepository.findById(authenticatorId)
                .orElseThrow(() -> new RuntimeException("Authenticator not found with ID: " + authenticatorId));

        // 2. Update only the safe fields using standard GETTERS
        if (updateDto.getFirstName() != null) {
            authenticator.setFirstName(updateDto.getFirstName());
        }
        if (updateDto.getLastName() != null) {
            authenticator.setLastName(updateDto.getLastName());
        }
        if (updateDto.getContactNumber() != null) {
            authenticator.setContactNumber(updateDto.getContactNumber());
        }
        if (updateDto.getProfessionalRegNumber() != null) {
            authenticator.setProfessionalRegNumber(updateDto.getProfessionalRegNumber());
        }

        // 3. Save the changes
        authenticatorRepository.save(authenticator);
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
            listing.setVerificationStatus(VerificationStatus.APPROVED);
        } else {
            // listing.setVerificationStatus("REJECTED");
            listing.setVerificationStatus(VerificationStatus.REJECTED);
        }

        listing.setLandAuthenticator(authenticator);
        listing.setVerifiedAt(new Date());

        // Save the updated listing
        landRepository.save(listing);
    }
}