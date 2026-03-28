package com.example.landapp.controller;

import com.example.landapp.dto.*;
import com.example.landapp.entity.Investor;
import com.example.landapp.entity.Owner;
import com.example.landapp.service.InvestorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/landapp/investors")
public class InvestorController {

    @Autowired
    private InvestorService investorService;

    // 2. SEARCH LAND (Filtering)
    @GetMapping("/search")
    public ResponseEntity<List<LandListingResponseDTO>> searchLand(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Double maxPrice) {

// 1. Capture the actual results from the service
        List<LandListingResponseDTO> searchResults = investorService.searchLandListings(keyword, maxPrice);

        // 2. Return the results as JSON!
        return new ResponseEntity<>(searchResults, HttpStatus.OK);
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

    @PostMapping("/listings/{listingId}/inquiry")
    public ResponseEntity<String> inquire(
            @PathVariable Long listingId,
            @RequestBody InquiryRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Investor currentInvestor = (Investor) authentication.getPrincipal();

        investorService.inquireAboutLand(currentInvestor.getId(), listingId, request.message());

        // Changed to just return a simple success string, since the detailed view is now in their dashboard
        return new ResponseEntity<>("Inquiry sent successfully to the owner.", HttpStatus.CREATED);
    }

    // 2. Investor checks their inquiries (Where they see the failsafe number!)
    @GetMapping("/inquiries")
    public ResponseEntity<List<InvestorInquiryResponseDTO>> getMyInquiries() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Investor currentInvestor = (Investor) authentication.getPrincipal();

        List<InvestorInquiryResponseDTO> responses = investorService.getInvestorInquiries(currentInvestor.getId());
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }



    @GetMapping("/me")
    public ResponseEntity<InvestorResponseDTO> getInvestor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Investor currentInvestor = (Investor) authentication.getPrincipal();

        InvestorResponseDTO response = investorService.getInvestorById(currentInvestor.getId());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*
      This endpoint allows the Investor to update their profile details.
    */
    @PutMapping("/me")
    public ResponseEntity<String> updateInvestorProfile(@RequestBody InvestorUpdateDTO updateDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Investor currentInvestor = (Investor) authentication.getPrincipal();

        investorService.updateInvestorProfile(currentInvestor.getId(), updateDto);
        return new ResponseEntity<>("Investor profile updated successfully", HttpStatus.OK);
    }
}

// Local records for incoming JSON payloads
record QuestionRequest(String content) {}
record ReviewRequest(int rating, String reviewText) {}
record InquiryRequest(String message) {}