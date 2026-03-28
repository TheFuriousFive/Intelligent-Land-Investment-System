package com.example.landapp.service;

import com.example.landapp.dto.AuthenticationDecisionDTO;
import com.example.landapp.dto.AuthenticatorUpdateDTO;
import com.example.landapp.dto.LandListingDetailDTO;
import com.example.landapp.entity.LandAuthenticator;
import com.example.landapp.entity.LandListing;
import com.example.landapp.entity.VerificationStatus;
import com.example.landapp.mapper.LandListingMapper;
import com.example.landapp.repository.LandAuthenticatorRepository;
import com.example.landapp.repository.LandListingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class LandAuthenticatorService {

    @Autowired
    private LandAuthenticatorRepository authenticatorRepository;

    @Autowired
    private LandListingRepository landRepository;

    @Autowired
    private LandListingMapper landMapper;

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
    public void authenticateLand(AuthenticationDecisionDTO decision) {

        // Find the authenticator
        LandAuthenticator authenticator = authenticatorRepository.findById(decision.getAuthenticatorId())
                .orElseThrow(() -> new RuntimeException("Authenticator not found"));

        // Find the land listing
        LandListing listing = landRepository.findById(decision.getListingId())
                .orElseThrow(() -> new RuntimeException("Land Listing not found"));

        // Update the listing's verification status
        // (You'll need to add a 'verificationStatus' and 'verifiedBy' field to your LandListing entity)
        if (decision.isApproved()) {
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

    // 1. Get all listings waiting for verification
    public List<LandListingDetailDTO> getPendingListings() {
        return landRepository.findByVerificationStatus(VerificationStatus.PENDING_VERIFICATION)
                .stream()
                .map(landMapper::toDetailDTO)
                .toList();
    }

    // 2. Get specific details when an authenticator clicks a listing
    public LandListingDetailDTO getListingById(Long id) {
        LandListing land = landRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Land not found with ID: "+ id));
        return landMapper.toDetailDTO(land);
    }


}