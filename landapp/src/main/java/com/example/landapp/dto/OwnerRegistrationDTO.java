package com.example.landapp.dto;

import lombok.Data;

@Data
public class OwnerRegistrationDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String contactNumber;
    private String password; // Raw password to be hashed
}