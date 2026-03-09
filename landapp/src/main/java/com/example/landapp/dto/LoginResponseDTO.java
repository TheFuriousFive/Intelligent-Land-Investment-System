package com.example.landapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {

    // The actual JWT token
    private String token;

    // How long until it expires (in milliseconds)
    private long expiresIn;

    // --- Extra fields to make your Next.js frontend development way easier ---
    private Long userId;
    private String firstName;
    private String userType; // e.g., "OWNER", "INVESTOR", or "AUTHENTICATOR"
}
