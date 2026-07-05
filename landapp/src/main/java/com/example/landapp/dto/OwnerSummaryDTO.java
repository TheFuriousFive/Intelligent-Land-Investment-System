package com.example.landapp.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OwnerSummaryDTO {
    private Long id;
    private String name; // Or their pseudonymous identity code!
    private String avatar; // Optional: If you add profile pics later
}
