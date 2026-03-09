package com.example.landapp.controller;

import com.example.landapp.dto.LandListingCreateDTO;
import com.example.landapp.dto.LandListingResponseDTO;
import com.example.landapp.dto.OwnerRegistrationDTO;
import com.example.landapp.dto.OwnerResponseDTO;
import com.example.landapp.service.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    @PostMapping("/{ownerId}/listings")
    public ResponseEntity<String> createListing(
            @PathVariable Long ownerId,
            @RequestBody LandListingCreateDTO dto) {

        // Security check: Make sure the URL ID matches the JSON payload ID
        if (!ownerId.equals(dto.getOwnerId())) {
            return new ResponseEntity<>("Owner ID mismatch", HttpStatus.BAD_REQUEST);
        }

        ownerService.createListing(dto);
        return new ResponseEntity<>("Land listing created successfully", HttpStatus.CREATED);
    }

    /*
      This is for the Owner to update create the land listing.
    */
    @PutMapping("/{ownerId}/listings/{listingId}")
    public ResponseEntity<String> updateListing(
            @PathVariable Long ownerId,
            @PathVariable Long listingId,
            @RequestBody LandListingCreateDTO dto) {

        // In the service layer, ensure this ownerId actually owns this listingId!
        ownerService.updateListing(listingId, dto);
        return new ResponseEntity<>("Land listing updated successfully", HttpStatus.OK);
    }

    /*
      This is for the Owner to delete  the land listing.
    */
    @DeleteMapping("/{ownerId}/listings/{listingId}")
    public ResponseEntity<String> deleteListing(
            @PathVariable Long ownerId,
            @PathVariable Long listingId) {

        ownerService.deleteListing(listingId, ownerId);
        return new ResponseEntity<>("Land listing deleted successfully", HttpStatus.OK);
    }

    /*
    For the Owner to answer a question regarding a landListing , to the Investor.
    */
    @PostMapping("/{ownerId}/questions/{questionId}/answer")
    public ResponseEntity<String> answerQuestion(
            @PathVariable Long ownerId,
            @PathVariable Long questionId,
            @RequestBody AnswerRequest request) {

        ownerService.answerQuestion(questionId, request.content());
        return new ResponseEntity<>("Question answered successfully", HttpStatus.OK);
    }

    /*
    This endpoint allows the Owner to view all their land listings.
     It returns a list of LandListingResponseDTOs, which contain the details of each listing.
     */
    @GetMapping("/{ownerId}/listings")
    public ResponseEntity<List<LandListingResponseDTO>> getOwnerListings(@PathVariable Long ownerId) {
        List<LandListingResponseDTO> listings = ownerService.getOwnerListings(ownerId);
        return new ResponseEntity<>(listings, HttpStatus.OK);
    }

    /*
  This endpoint fetches a specific owner's profile by their ID.
*/
    @GetMapping("/{ownerId}")
    public ResponseEntity<OwnerResponseDTO> getOwner(@PathVariable Long ownerId) {
        OwnerResponseDTO response = ownerService.getOwnerById(ownerId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }




}

// A quick local record just to catch the incoming JSON answer text!
record AnswerRequest(String content) {}