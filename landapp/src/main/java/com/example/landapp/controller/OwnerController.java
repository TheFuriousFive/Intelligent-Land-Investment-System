package com.example.landapp.controller;
import com.example.landapp.dto.*;
import com.example.landapp.entity.ContactMethod;
import com.example.landapp.entity.Owner;
import com.example.landapp.service.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/landapp/owners")
public class OwnerController {

    @Autowired
    private OwnerService ownerService;

    /*
      This is for the Owner to create a new land listing WITH images and deeds.
     */
    @PostMapping(value = "/listings", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    // FIXED: Changed signature to Map<String, Object>
    public ResponseEntity<java.util.Map<String, Object>> createListing(
            @RequestPart("landData") LandListingCreateDTO dto,
            @RequestPart("images") List<MultipartFile> images,
            @RequestPart("deedDocument") MultipartFile deedDocument) {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Owner currentOwner = (Owner) authentication.getPrincipal();

            dto.setOwnerId(currentOwner.getId());

            // 1. Call the service ONCE and catch the full DTO it returns
            LandListingResponseDTO savedListing = ownerService.createListing(dto, images, deedDocument);

            // 2. Extract the ID from that saved DTO
            Long newListingId = savedListing.getId();

            // 3. Return the ID back to the Next.js frontend!
            return new ResponseEntity<>(
                    java.util.Map.of(
                            "message", "Land listing created successfully",
                            "listingId", newListingId
                    ),
                    HttpStatus.CREATED
            );

            // (Deleted the duplicate string return statement that was here)

        } catch (Exception e) {
            // FIXED: Make the error response a Map as well to match the signature
            return new ResponseEntity<>(
                    java.util.Map.of("error", "Failed to create listing: " + e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
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

        ownerService.answerQuestion(questionId, currentOwner.getId(),request.content());
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

    @GetMapping("/inquiries")
    public ResponseEntity<List<OwnerInboxResponseDTO>> getMyInquiries() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Owner currentOwner = (Owner) authentication.getPrincipal();

        List<OwnerInboxResponseDTO> inquiries = ownerService.getOwnerInquiries(currentOwner.getId());
        return new ResponseEntity<>(inquiries, HttpStatus.OK);
    }

    // Owner clicks "Reply via Email" or "Reply via Phone"
    @PostMapping("/inquiries/{inquiryId}/reply")
    public ResponseEntity<String> replyToInquiry(
            @PathVariable Long inquiryId,
            @RequestBody ReplyRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Owner currentOwner = (Owner) authentication.getPrincipal();

        ownerService.replyToInquiry(inquiryId, currentOwner.getId(), request.method());
        return new ResponseEntity<>("Inquiry updated successfully", HttpStatus.OK);
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

    @PutMapping("/me")
    public ResponseEntity<String> updateOwnerProfile(@RequestBody OwnerUpdateDTO updateDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Owner currentOwner = (Owner) authentication.getPrincipal();

        ownerService.updateOwnerProfile(currentOwner.getId(), updateDto);
        return new ResponseEntity<>("Owner profile updated successfully", HttpStatus.OK);
    }
}

// A quick local record just to catch the incoming JSON answer text!
record AnswerRequest(String content) {}
record ReplyRequest(ContactMethod method) {}