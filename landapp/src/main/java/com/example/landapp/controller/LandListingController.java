package com.example.landapp.controller;

import com.example.landapp.dto.LandListingResponseDTO;
import com.example.landapp.dto.QuestionResponseDTO;
import com.example.landapp.dto.ReviewResponseDTO;
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
}