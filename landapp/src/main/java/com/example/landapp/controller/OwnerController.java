package com.example.landapp.controller;

import com.example.landapp.dto.LandListingCreateDTO;
import com.example.landapp.dto.LandListingResponseDTO;
import com.example.landapp.dto.OwnerResponseDTO;
import com.example.landapp.entity.Owner;
import com.example.landapp.service.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/landapp/owners")
public class OwnerController {

    @Autowired
    private OwnerService ownerService;

    /*
      This is for the Owner to create a new land listing.
     */
    @PostMapping("/listings")
    public ResponseEntity<String> createListing(@RequestBody LandListingCreateDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Owner currentOwner = (Owner) authentication.getPrincipal();

        dto.setOwnerId(currentOwner.getId());

        ownerService.createListing(dto);
        return new ResponseEntity<>("Land listing created successfully", HttpStatus.CREATED);
    }

    /*
      This is for the Owner to update the land listing.
    */
    @PutMapping("/listings/{listingId}")
    public ResponseEntity<String> updateListing(
            @PathVariable Long listingId,
            @RequestBody LandListingCreateDTO dto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Owner currentOwner = (Owner) authentication.getPrincipal();

        // Pass the secure ID to ensure the service knows WHO is updating this!
        dto.setOwnerId(currentOwner.getId());
        ownerService.updateListing(listingId, dto);

        return new ResponseEntity<>("Land listing updated successfully", HttpStatus.OK);
    }

    /*
      This is for the Owner to delete the land listing.
    */
    @DeleteMapping("/listings/{listingId}")
    public ResponseEntity<String> deleteListing(@PathVariable Long listingId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Owner currentOwner = (Owner) authentication.getPrincipal();

        ownerService.deleteListing(listingId, currentOwner.getId());
        return new ResponseEntity<>("Land listing deleted successfully", HttpStatus.OK);
    }

    /*
    For the Owner to answer a question regarding a landListing , to the Investor.
    */
    @PostMapping("/questions/{questionId}/answer")
    public ResponseEntity<String> answerQuestion(
            @PathVariable Long questionId,
            @RequestBody AnswerRequest request) {

        // Note: Your service might need to be updated to accept the ownerId
        // to verify they actually own the listing this question is attached to!
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Owner currentOwner = (Owner) authentication.getPrincipal();

        ownerService.answerQuestion(questionId, request.content());
        return new ResponseEntity<>("Question answered successfully", HttpStatus.OK);
    }

    /*
    This endpoint allows the Owner to view all their land listings.
    */
    @GetMapping("/listings")
    public ResponseEntity<List<LandListingResponseDTO>> getOwnerListings() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Owner currentOwner = (Owner) authentication.getPrincipal();

        List<LandListingResponseDTO> listings = ownerService.getOwnerListings(currentOwner.getId());
        return new ResponseEntity<>(listings, HttpStatus.OK);
    }

    /*
      This endpoint fetches the logged-in owner's profile.
      Changed path from /{ownerId} to /me for better REST practice!
    */
    @GetMapping("/me")
    public ResponseEntity<OwnerResponseDTO> getOwner() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Owner currentOwner = (Owner) authentication.getPrincipal();

        OwnerResponseDTO response = ownerService.getOwnerById(currentOwner.getId());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

// A quick local record just to catch the incoming JSON answer text!
record AnswerRequest(String content) {}