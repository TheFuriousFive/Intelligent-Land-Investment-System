package com.example.landapp.controller;

import com.example.landapp.dto.InvestorRegistrationDTO;
import com.example.landapp.dto.InvestorResponseDTO;
import com.example.landapp.service.InvestorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/landapp/investors")
public class InvestorController {

    @Autowired
    private InvestorService investorService;



    // 2. SEARCH LAND (Filtering)
    @GetMapping("/search")
    public ResponseEntity<String> searchLand(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Double maxPrice) {

        investorService.searchLandListings(keyword, maxPrice);
        return new ResponseEntity<>("Search criteria received (Logic pending in Service)", HttpStatus.OK);
    }

    // 3. ASK A QUESTION
    @PostMapping("/{investorId}/listings/{listingId}/questions")
    public ResponseEntity<String> askQuestion(
            @PathVariable Long investorId,
            @PathVariable Long listingId,
            @RequestBody QuestionRequest request) {

        investorService.askQuestion(investorId, listingId, request.content());
        return new ResponseEntity<>("Question submitted successfully", HttpStatus.CREATED);
    }

    // 4. SUBMIT A REVIEW
    @PostMapping("/{investorId}/listings/{listingId}/reviews")
    public ResponseEntity<String> submitReview(
            @PathVariable Long investorId,
            @PathVariable Long listingId,
            @RequestBody ReviewRequest request) {

        investorService.submitReview(investorId, listingId, request.rating(), request.reviewText());
        return new ResponseEntity<>("Review submitted successfully", HttpStatus.CREATED);
    }

    // 5. INQUIRE ABOUT LAND
    @PostMapping("/{investorId}/listings/{listingId}/inquiry")
    public ResponseEntity<String> inquire(
            @PathVariable Long investorId,
            @PathVariable Long listingId) {

        investorService.inquireAboutLand(investorId, listingId);
        return new ResponseEntity<>("Inquiry sent to the owner", HttpStatus.OK);
    }
}

// Local records for incoming JSON payloads
record QuestionRequest(String content) {}
record ReviewRequest(int rating, String reviewText) {}