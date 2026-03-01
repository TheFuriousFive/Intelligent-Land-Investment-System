package com.example.landapp.dto;

import lombok.Data;

@Data
public class InvestorResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String contactNumber;
    // Password is safely excluded!
}