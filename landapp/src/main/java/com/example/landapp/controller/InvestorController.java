package com.example.landapp.controller;

import com.example.landapp.entity.Investor;
import com.example.landapp.service.InvestorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    @PostMapping("/listings/{listingId}/questions")
    public ResponseEntity<String> askQuestion(
            @PathVariable Long listingId,
            @RequestBody QuestionRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Investor currentInvestor = (Investor) authentication.getPrincipal();

        investorService.askQuestion(currentInvestor.getId(), listingId, request.content());
        return new ResponseEntity<>("Question submitted successfully", HttpStatus.CREATED);
    }

    // 4. SUBMIT A REVIEW
    @PostMapping("/listings/{listingId}/reviews")
    public ResponseEntity<String> submitReview(
            @PathVariable Long listingId,
            @RequestBody ReviewRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Investor currentInvestor = (Investor) authentication.getPrincipal();

        investorService.submitReview(currentInvestor.getId(), listingId, request.rating(), request.reviewText());
        return new ResponseEntity<>("Review submitted successfully", HttpStatus.CREATED);
    }

    // 5. INQUIRE ABOUT LAND
    @PostMapping("/listings/{listingId}/inquiry")
    public ResponseEntity<String> inquire(@PathVariable Long listingId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Investor currentInvestor = (Investor) authentication.getPrincipal();

        investorService.inquireAboutLand(currentInvestor.getId(), listingId);
        return new ResponseEntity<>("Inquiry sent to the owner", HttpStatus.OK);
    }
}

// Local records for incoming JSON payloads
record QuestionRequest(String content) {}
record ReviewRequest(int rating, String reviewText) {}