package com.example.landapp.controller;

import com.example.landapp.dto.*;
import com.example.landapp.entity.BaseUser;
import com.example.landapp.entity.Investor;
import com.example.landapp.entity.LandAuthenticator;
import com.example.landapp.entity.Owner;
import com.example.landapp.service.AuthenticationService;
import com.example.landapp.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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
    public ResponseEntity<Owner> registerOwner(@RequestBody OwnerRegistrationDTO registerDto) {
        Owner registeredOwner = authenticationService.registerOwner(registerDto);
        return ResponseEntity.ok(registeredOwner);
    }

    // 2. REGISTER INVESTOR
    @PostMapping("/signup/investor")
    public ResponseEntity<Investor> registerInvestor(@RequestBody InvestorRegistrationDTO registerDto) {
        Investor registeredInvestor = authenticationService.registerInvestor(registerDto);
        return ResponseEntity.ok(registeredInvestor);
    }

    @PostMapping("/signup/authenticator")
    public ResponseEntity<LandAuthenticator> registerAuthenticator(@RequestBody LandAuthenticatorRegistrationDTO registerDto) {
        LandAuthenticator registeredAuthenticator = authenticationService.registerAuthenticator(registerDto);
        return ResponseEntity.ok(registeredAuthenticator);
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
        } else if (authenticatedUser instanceof LandAuthenticator) {
            userType = "AUTHENTICATOR";
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
                .ricCode(authenticatedUser.getRicCode())
                .build();

        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Grab the base user (could be Owner OR Investor)
        BaseUser currentUser = (BaseUser) authentication.getPrincipal();

        // Build a response that tells the frontend exactly what type of user this is
        Map<String, Object> response = new HashMap<>();
        response.put("id", currentUser.getId());
        response.put("firstName", currentUser.getFirstName());
        response.put("email", currentUser.getEmail());

        if (currentUser instanceof Owner) {
            response.put("userType", "OWNER");
        } else if (currentUser instanceof Investor) {
            response.put("userType", "INVESTOR");
        } else if (currentUser instanceof LandAuthenticator) {
            response.put("userType", "AUTHENTICATOR");
        } else {
            response.put("userType", "UNKNOWN");
        }

        return ResponseEntity.ok(response);
    }
}
