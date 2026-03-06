package com.example.landapp.service;

import com.example.landapp.dto.LandListingCreateDTO;
import com.example.landapp.dto.LandListingResponseDTO;
import com.example.landapp.dto.OwnerRegistrationDTO;
import com.example.landapp.dto.OwnerResponseDTO;
import com.example.landapp.entity.LandListing;
import com.example.landapp.entity.Owner;
import com.example.landapp.mapper.LandListingMapper;
import com.example.landapp.mapper.OwnerMapper;
import com.example.landapp.repository.LandListingRepository;
import com.example.landapp.repository.OwnerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OwnerService {

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private OwnerMapper ownerMapper;

    @Autowired
    private LandListingRepository landRepository;

    @Autowired
    private LandListingMapper landMapper;


    public OwnerResponseDTO registerOwner(OwnerRegistrationDTO registrationDTO) {
        //  Convert DTO to Entity using the mapper class
        Owner owner = ownerMapper.toEntity(registrationDTO);

        //This is for the hashing logic . although for now this is just
        // fake hashing (Must implement this later !)
        owner.setPasswordHash("SAFE_HASH_" + registrationDTO.getPassword());

        // 3. Save to Database
        Owner savedOwner = ownerRepository.save(owner);

        // 4. Return the Response DTO (clean data)
        return ownerMapper.toResponseDTO(savedOwner);
    }

    // 1. CREATE Listing
    @Transactional
    public LandListingResponseDTO createListing(LandListingCreateDTO dto) {
        Owner owner = ownerRepository.findById(dto.getOwnerId())
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        LandListing listing = landMapper.toEntity(dto);

        listing.setOwner(owner);

        LandListing savedListing = landRepository.save(listing);

        return landMapper.toResponseDTO(savedListing);

    }

    // 2. DELETE Listing
    @Transactional
    public void deleteListing(Long listingId, Long ownerId) {

        LandListing listing = landRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        if (!listing.getOwner().getId().equals(ownerId)){
            throw new RuntimeException("Unauthorized: You can only delete your own listings");
        }

        landRepository.delete(listing);

    }

    // 3. UPDATE Listing
    @Transactional
    public void updateListing(Long listingId, LandListingCreateDTO updateDto) {
        LandListing existingListing = landRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        if (!existingListing.getOwner().getId().equals(updateDto.getOwnerId())){
            throw new RuntimeException("Unauthorized: You can only update your own listings");
        }

        // Update the existing entity with new values from the DTO
        landMapper.updateEntityFromDto(updateDto, existingListing);

        landRepository.save(existingListing);

    }

    public void answerQuestion(Long questionId, String answerContent) {
        // TO DO
    }

    public List<LandListingResponseDTO> getOwnerListings(Long ownerId) {
        // 1. Fetch the raw entities from the database
        List<LandListing> listings = landRepository.findByOwnerId(ownerId);

        // 2. Convert them to Response DTOs and return the list
        return listings.stream()
                .map(landMapper::toResponseDTO)
                .toList();
    }

    public OwnerResponseDTO getOwnerById(Long ownerId) {
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found"));
        return ownerMapper.toResponseDTO(owner);
    }
}