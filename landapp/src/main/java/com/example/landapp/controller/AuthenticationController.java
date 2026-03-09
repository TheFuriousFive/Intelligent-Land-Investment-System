package com.example.landapp.controller;

import com.example.landapp.dto.*;
import com.example.landapp.entity.BaseUser;
import com.example.landapp.entity.Investor;
import com.example.landapp.entity.Owner;
import com.example.landapp.service.AuthenticationService;
import com.example.landapp.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    // 1. REGISTER OWNER
    @PostMapping("/signup/owner")
    public ResponseEntity<OwnerResponseDTO> registerOwner(@RequestBody OwnerRegistrationDTO registerDto) {
        OwnerResponseDTO registeredOwner = authenticationService.registerOwner(registerDto);
        return ResponseEntity.ok(registeredOwner);
    }

    // 2. REGISTER INVESTOR
    @PostMapping("/signup/investor")
    public ResponseEntity<InvestorResponseDTO> registerInvestor(@RequestBody InvestorRegistrationDTO registerDto) {
        InvestorResponseDTO registeredInvestor = authenticationService.registerInvestor(registerDto);
        return ResponseEntity.ok(registeredInvestor);
    }

    // 3. LOGIN (Handles all user types!)
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> authenticate(@RequestBody LoginDTO loginDto) {
        // Authenticate the user (throws error if bad password)
        BaseUser authenticatedUser = authenticationService.authenticate(loginDto);

        // Generate the JWT token
        String jwtToken = jwtService.generateToken(authenticatedUser);

        // Figure out what type of user just logged in so Next.js knows which dashboard to show
        String userType = "UNKNOWN";
        if (authenticatedUser instanceof Owner) {
            userType = "OWNER";
        } else if (authenticatedUser instanceof Investor) {
            userType = "INVESTOR";
        }

        // Build the custom LoginResponseDTO we created earlier
        LoginResponseDTO loginResponse = LoginResponseDTO.builder()
                .token(jwtToken)
                .expiresIn(jwtService.getExpirationTime())
                .userId(authenticatedUser.getId())
                .firstName(authenticatedUser.getFirstName())
                .userType(userType)
                .build();

        return ResponseEntity.ok(loginResponse);
    }
}
