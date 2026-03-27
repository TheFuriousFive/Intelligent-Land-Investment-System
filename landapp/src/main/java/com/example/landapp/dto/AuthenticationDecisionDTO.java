package com.example.landapp.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationDecisionDTO {
    private long ListingId;
    private long authenticatorId;
    private long approved;
    private String comments;
}
