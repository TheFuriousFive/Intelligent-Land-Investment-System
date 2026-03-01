package com.example.landapp.mapper;

import com.example.landapp.dto.OwnerRegistrationDTO;
import com.example.landapp.dto.OwnerResponseDTO;
import com.example.landapp.entity.Owner;
import org.springframework.stereotype.Component;

@Component
public class OwnerMapper {


    public Owner toEntity(OwnerRegistrationDTO dto) { //  Registration Request -> Entity
        if (dto == null) return null;

        Owner owner = new Owner();
        owner.setFirstName(dto.getFirstName());
        owner.setLastName(dto.getLastName());
        owner.setEmail(dto.getEmail());
        owner.setContactNumber(dto.getContactNumber());
        // passwordHash will be set in the Service after hashing
        return owner;
    }


    public OwnerResponseDTO toResponseDTO(Owner owner) { // Entity -> Response DTO (for the Frontend)
        if (owner == null) return null;

        OwnerResponseDTO dto = new OwnerResponseDTO();
        dto.setId(owner.getId());
        dto.setFirstName(owner.getFirstName());
        dto.setLastName(owner.getLastName());
        dto.setEmail(owner.getEmail());
        dto.setContactNumber(owner.getContactNumber());
        dto.setTrustScore(owner.getTrustScore());
        return dto;
    }
}