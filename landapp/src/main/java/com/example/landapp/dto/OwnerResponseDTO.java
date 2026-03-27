package com.example.landapp.dto;

import lombok.Data;

@Data
public class OwnerResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String contactNumber;
    private Double trustScore;
    private String ricCode;
    // Note: We excluded passwordHash for security!
}