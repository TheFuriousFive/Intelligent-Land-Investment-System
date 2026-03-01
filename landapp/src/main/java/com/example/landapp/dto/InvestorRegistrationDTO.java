package com.example.landapp.dto;

import lombok.Data;

@Data
public class InvestorRegistrationDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String contactNumber;
    private String password; // Raw password to be hashed
}