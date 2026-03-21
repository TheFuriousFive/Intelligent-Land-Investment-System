package com.example.landapp.dto;

import lombok.Data;

@Data
public class AuthenticatorUpdateDTO {
    private String firstName;
    private String lastName;
    private String contactNumber;
    private String professionalRegNumber;
}
