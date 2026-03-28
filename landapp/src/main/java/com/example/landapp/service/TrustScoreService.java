package com.example.landapp.service;

import com.example.landapp.entity.*;
import com.example.landapp.repository.InquiryRepository;
import com.example.landapp.repository.LandListingRepository;
import com.example.landapp.repository.OwnerRepository;
import com.example.landapp.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrustScoreService {

    @Autowired
    private LandListingRepository landRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private InquiryRepository inquiryRepository;

    @Transactional
    public Double calculateTrustScore(Long ownerId) {
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        double totalScore = 0.0;

        // ---------------------------------------------------------
        // 1. Profile Completeness (Max 10 Points)
        // ---------------------------------------------------------
        if (owner.getFirstName() != null && !owner.getFirstName().trim().isEmpty() &&
                owner.getLastName() != null && !owner.getLastName().trim().isEmpty()) {
            totalScore += 5.0; // 5 points for having a name
        }
        if (owner.getContactNumber() != null && !owner.getContactNumber().trim().isEmpty()) {
            totalScore += 5.0; // 5 points for having a phone number
        }

        // ---------------------------------------------------------
        // 2. Reviews & Ratings (Max 50 Points)
        // ---------------------------------------------------------
        List<Review> reviews = reviewRepository.findByOwnerId(ownerId);
        if (!reviews.isEmpty()) {
            // Calculate the average star rating
            double sumRatings = reviews.stream().mapToInt(Review::getRating).sum();
            double averageRating = sumRatings / reviews.size();

            // Formula: (Avg Rating / 5.0) * 50
            // Example: 4.5 stars = 45 points
            totalScore += (averageRating / 5.0) * 50.0;
        } else {
            // Baseline for new owners so they don't look like scammers
            totalScore += 25.0;
        }

        // ---------------------------------------------------------
        // 3. Authenticated Listings Ratio (Max 30 Points)
        // ---------------------------------------------------------
        List<LandListing> allListings = landRepository.findByOwnerId(ownerId);
        if (!allListings.isEmpty()) {
            // Count how many lands the Authenticator actually approved
            long verifiedCount = allListings.stream()
                    .filter(l -> l.getVerificationStatus() == VerificationStatus.APPROVED)
                    .count();

            // Percentage of their total lands that are verified
            double verificationRatio = (double) verifiedCount / allListings.size();
            totalScore += (verificationRatio * 30.0);
        } else {
            // Baseline if they haven't uploaded lands yet
            totalScore += 15.0;
        }

        // ---------------------------------------------------------
        // 4. Responsiveness (Max 10 Points)
        // ---------------------------------------------------------
        List<Inquiry> inquiries = inquiryRepository.findByOwnerIdOrderByCreatedAtDesc(ownerId);
        if (!inquiries.isEmpty()) {
            // Count how many inquiries the Owner replied to (CONTACTED or CLOSED)
            // If it's still PENDING, it means they ignored it!
            long repliedCount = inquiries.stream()
                    .filter(i -> i.getStatus() == InquiryStatus.CONTACTED || i.getStatus() == InquiryStatus.CLOSED)
                    .count();

            // Percentage of inquiries they actually answered
            double replyRatio = (double) repliedCount / inquiries.size();
            totalScore += (replyRatio * 10.0);
        } else {
            // Baseline if nobody has messaged them yet
            totalScore += 5.0;
        }

        // ---------------------------------------------------------
        // Finalize and Save
        // ---------------------------------------------------------
        // A failsafe to guarantee the score never breaks out of the 0-100 boundary
        totalScore = Math.max(0.0, Math.min(100.0, totalScore));

        // Format to 1 decimal place (e.g., 87.5) to keep the DB clean
        totalScore = Math.round(totalScore * 10.0) / 10.0;

        owner.setTrustScore(totalScore);
        ownerRepository.save(owner);

        return totalScore;
    }
}