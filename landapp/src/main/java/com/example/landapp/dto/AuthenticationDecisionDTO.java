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
    private Long listingId;
    private Long authenticatorId;
    private boolean approved;
    private String comments;

}