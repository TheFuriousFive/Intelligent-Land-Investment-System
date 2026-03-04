package com.example.landapp.mapper;

import com.example.landapp.dto.LandListingCreateDTO;
import com.example.landapp.dto.LandListingResponseDTO;
import com.example.landapp.entity.LandListing;
import org.springframework.stereotype.Component;

@Component
public class LandListingMapper {

    // 1. DTO -> Entity (For Creating)
    public LandListing toEntity(LandListingCreateDTO dto) {
        if (dto == null) return null;

        LandListing listing = new LandListing();
        listing.setTitle(dto.getTitle());
        listing.setDescription(dto.getDescription());
        listing.setPrice(dto.getPrice());
        listing.setArea(dto.getArea());
        listing.setLocation(dto.getLocation());
        listing.setLandType(dto.getLandType());

        // owner, status, and postedDate are handled in the Service or @PrePersist
        return listing;
    }

    // 2. Entity -> Response DTO (For Reading)
    public LandListingResponseDTO toResponseDTO(LandListing entity) {
        if (entity == null) return null;

        LandListingResponseDTO dto = new LandListingResponseDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setPrice(entity.getPrice());
        dto.setArea(entity.getArea());
        dto.setLocation(entity.getLocation());
        dto.setLandType(entity.getLandType());
        dto.setPostedDate(entity.getPostedDate());

        // Convert the Enum to a String safely
        if (entity.getStatus() != null) {
            dto.setStatus(entity.getStatus().name());
        }

        // Flatten the owner data safely (assuming Owner has firstName/lastName like Investor)
        if (entity.getOwner() != null) {
            dto.setOwnerName(entity.getOwner().getFirstName() + " " + entity.getOwner().getLastName());
        }

        return dto;
    }

    // 3. Update Existing Entity (Keeps your Service layer clean)
    public void updateEntityFromDto(LandListingCreateDTO dto, LandListing existing) {
        if (dto == null || existing == null) return;

        existing.setTitle(dto.getTitle());
        existing.setDescription(dto.getDescription());
        existing.setPrice(dto.getPrice());
        existing.setArea(dto.getArea());
        existing.setLocation(dto.getLocation());
        existing.setLandType(dto.getLandType());

        // Note: We intentionally do NOT update the ID, Owner, Status, or Date here.
    }
}
