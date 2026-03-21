package com.example.landapp.dto;

import java.math.BigDecimal;

public record OwnerUpdateDTO(
        String firstName,
        String lastName,
        String contactNumber
) {}