package com.example.landapp.dto;

import lombok.Data;

@Data
public class LandAuthenticatorRegistrationDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String contactNumber;
    private String passwordHash;
    private String professionalRegNumber; // The unique field!
}