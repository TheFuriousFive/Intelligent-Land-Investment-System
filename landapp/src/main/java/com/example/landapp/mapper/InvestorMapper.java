package com.example.landapp.mapper;

import com.example.landapp.dto.InvestorRegistrationDTO;
import com.example.landapp.dto.InvestorResponseDTO;
import com.example.landapp.entity.Investor;
import org.springframework.stereotype.Component;

@Component
public class InvestorMapper {


    public Investor toEntity(InvestorRegistrationDTO dto) {//  Request -> Entity
        if (dto == null) return null;

        Investor investor = new Investor();
        investor.setFirstName(dto.getFirstName());
        investor.setLastName(dto.getLastName());
        investor.setEmail(dto.getEmail());
        investor.setContactNumber(dto.getContactNumber());
        // passwordHash will be handled in the Service
        return investor;
    }


    public InvestorResponseDTO toResponseDTO(Investor investor) { // Entity -> Response DTO
        if (investor == null) return null;

        InvestorResponseDTO dto = new InvestorResponseDTO();
        dto.setId(investor.getId());
        dto.setFirstName(investor.getFirstName());
        dto.setLastName(investor.getLastName());
        dto.setEmail(investor.getEmail());
        dto.setContactNumber(investor.getContactNumber());
        return dto;
    }
}