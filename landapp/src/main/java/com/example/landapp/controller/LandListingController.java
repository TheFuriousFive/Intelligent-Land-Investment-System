package com.example.landapp.controller;

import com.example.landapp.dto.*;
import com.example.landapp.service.LandAuthenticatorService;
import com.example.landapp.service.LandListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/listings")
public class LandListingController {

    @Autowired
    private LandListingService landListingService;

    @Autowired
    private LandAuthenticatorService authenticatorService;

    // 1. Fetch the main property details
    @GetMapping("/{listingId}")
    public ResponseEntity<LandListingResponseDTO> getListing(@PathVariable Long listingId) {
        LandListingResponseDTO response = landListingService.getListingById(listingId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 2. Fetch the Q&A section
    @GetMapping("/{listingId}/questions")
    public ResponseEntity<List<QuestionResponseDTO>> getListingQuestions(@PathVariable Long listingId) {
        List<QuestionResponseDTO> responses = landListingService.getListingQuestions(listingId);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    // 3. Fetch the reviews for the property owner
    @GetMapping("/{listingId}/reviews")
    public ResponseEntity<List<ReviewResponseDTO>> getListingReviews(@PathVariable Long listingId) {
        List<ReviewResponseDTO> responses = landListingService.getListingReviews(listingId);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    // 4. Fetch ALL land listings for the public catalog
    @GetMapping
    public ResponseEntity<List<LandListingResponseDTO>> getAllListings() {
        List<LandListingResponseDTO> responses = landListingService.getAllListings();
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    // Submit approval or rejection
    @PostMapping("/verify-land")
    public ResponseEntity<String> verifyLand(@RequestBody AuthenticationDecisionDTO decision) {
        authenticatorService.authenticateLand(decision);
        return ResponseEntity.ok("Verification status updated to: " + (decision.isApproved() ? "APPROVED" : "REJECTED"));
    }

    // GET: Display the "Inbox" of pending lands
    @GetMapping("/pending")
    public ResponseEntity<List<LandListingDetailDTO>> getPendingListings() {
        return ResponseEntity.ok(authenticatorService.getPendingListings());
    }

    // GET: Display full details when a specific land is clicked
    @GetMapping("/details/{id}")
    public ResponseEntity<LandListingDetailDTO> getListingDetails(@PathVariable Long id) {
        return ResponseEntity.ok(authenticatorService.getListingById(id));
    }

}