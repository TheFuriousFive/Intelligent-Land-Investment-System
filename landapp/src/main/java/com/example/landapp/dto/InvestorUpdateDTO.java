package com.example.landapp.dto;

import java.math.BigDecimal;

public record InvestorUpdateDTO(
        String firstName,
        String lastName,
        String contactNumber,
        String preferredLocation,
        BigDecimal investmentBudget
) {}
