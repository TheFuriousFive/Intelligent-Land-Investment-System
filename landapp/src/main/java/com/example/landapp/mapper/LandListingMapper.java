package com.example.landapp.mapper;

import com.example.landapp.dto.LandListingCreateDTO;
import com.example.landapp.dto.LandListingDetailDTO;
import com.example.landapp.dto.LandListingResponseDTO;
import com.example.landapp.entity.LandListing;
import com.example.landapp.service.MapDataGetting;
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
        listing.setLatitude(dto.getLatitude());
        listing.setLongitude(dto.getLongitude());

        // owner, status, and postedDate are handled in the Service or @PrePersist
        return listing;
    }

    public void addMapDataToEntity(LandListing entity, MapDataGetting.MapDataResult mapData) {
        if (mapData == null) return;

        // Saving specific OSM audit data to the database
        entity.setOsmLandUse(mapData.getSuggestedLandType());
        entity.setOsmAmenities(mapData.getAmenities());
        entity.setOsmAccessRoad(mapData.getAccessRoad());

        // Fill the main LandType if the user left it empty on the website form
        if (entity.getLandType() == null || entity.getLandType().isEmpty()) {
            entity.setLandType(mapData.getSuggestedLandType());
        }
    }

    // 2. Entity -> Response DTO (For Reading)
    public LandListingResponseDTO toResponseDTO(LandListing land) {
        if (land == null) return null;

        LandListingResponseDTO dto = new LandListingResponseDTO();
        dto.setId(land.getId());
        dto.setTitle(land.getTitle());
        dto.setDescription(land.getDescription());
        dto.setPrice(land.getPrice());
        dto.setArea(land.getArea());
        dto.setLocation(land.getLocation());
        dto.setLandType(land.getLandType());
        dto.setPostedDate(land.getPostedDate());
        dto.setVerifiedAt(land.getVerifiedAt());
        dto.setImageUrls(land.getImageUrls());
        dto.setDeedDocumentUrls(land.getDeedDocumentUrls());

        // Convert the Enum to a String safely
        if (land.getStatus() != null) {
            dto.setStatus(land.getStatus().name());
        }
        if (land.getVerificationStatus() != null) {
            dto.setVerificationStatus(land.getVerificationStatus().name());
        }

        // Flatten the owner data safely (assuming Owner has firstName/lastName like Investor)
        if (land.getOwner() != null) {
            dto.setOwnerName(land.getOwner().getFirstName() + " " + land.getOwner().getLastName());
        }

        if (land.getLandAuthenticator() != null) {
            dto.setAuthenticatorRegNumber(
                    land.getLandAuthenticator().getProfessionalRegNumber()
            );
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

    public LandListingDetailDTO toDetailDTO(LandListing land) {
        if (land == null) {
            return null;
        }
        return LandListingDetailDTO.builder()
                .id(land.getId())
                .title(land.getTitle())
                .description(land.getDescription())
                .price(land.getPrice())
                .area(land.getArea())
                .location(land.getLocation())
                .landType(land.getLandType())
                .verificationStatus(land.getVerificationStatus())
                .imageUrls(land.getImageUrls())
                .deedDocumentUrls(land.getDeedDocumentUrls())
                // Safe null check for owner
                .ownerName(land.getOwner() != null ?
                        land.getOwner().getFirstName() + " " + land.getOwner().getLastName() :
                        "N/A")
                .latitude(land.getLatitude())
                .longitude(land.getLongitude())
                .build();
    }
}
