package com.example.landapp.service;

import com.example.landapp.entity.LandListing;
import com.example.landapp.entity.ListingStatus;
import com.example.landapp.entity.VerificationStatus;
import com.example.landapp.entity.Owner;
import com.example.landapp.repository.LandListingRepository;
import com.example.landapp.repository.OwnerRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.landapp.entity.ListingStatus;
import java.util.List;

@Service
public class TrustScoreService {

    @Autowired
    private LandListingRepository landRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    public Double calculateTrustScore(Long ownerId) {
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        double totalScore = 0.0;

        //  Profile Completeness
        if (owner.getFirstName() != null && !owner.getFirstName().trim().isEmpty() &&
                owner.getLastName() != null && !owner.getLastName().trim().isEmpty()) {
            totalScore += 10.0;
        }
        if (owner.getContactNumber() != null && !owner.getContactNumber().trim().isEmpty()) {
            totalScore += 10.0;
        }

        // Sales History (Max 40 points)
        List<LandListing> soldListings = landRepository.findByOwnerIdAndStatus(ownerId, ListingStatus.SOLD);
        double salesScore = soldListings.size() * 10.0;
        totalScore += Math.min(salesScore, 40.0);

        //  Authenticated Listings
        List<LandListing> verifiedListings = landRepository.findByOwnerIdAndVerificationStatus(ownerId, VerificationStatus.VERIFIED);
        double verificationScore = verifiedListings.size() * 10.0;
        totalScore += Math.min(verificationScore, 40.0);

        // Update the owner's entity and save it to the database
        owner.setTrustScore(totalScore);
        ownerRepository.save(owner);

        return totalScore;
    }
}