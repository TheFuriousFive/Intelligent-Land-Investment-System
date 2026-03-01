package com.example.landapp.service;

import com.example.landapp.dto.LandListingCreateDTO;
import com.example.landapp.dto.OwnerRegistrationDTO;
import com.example.landapp.dto.OwnerResponseDTO;
import com.example.landapp.entity.Owner;
import com.example.landapp.mapper.OwnerMapper;
import com.example.landapp.repository.OwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OwnerService {

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private OwnerMapper ownerMapper;


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
    public void createListing(LandListingCreateDTO dto) {
        //TO DO
    }

    // 2. DELETE Listing
    public void deleteListing(Long listingId, Long ownerId) {
        // TO DO

    }

    // 3. UPDATE Listing
    public void updateListing(Long listingId, LandListingCreateDTO updateDto) {
        // TO DO

    }

    public void answerQuestion(Long questionId, String answerContent) {
        // TO DO
    }
}