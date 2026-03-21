package com.example.landapp.controller;

import com.example.landapp.dto.AuthenticatorUpdateDTO;
import com.example.landapp.entity.LandAuthenticator;
import com.example.landapp.service.LandAuthenticatorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/landapp/verifiers")
public class LandVerifierController {

    private final LandAuthenticatorService authenticatorService;

    public LandVerifierController(LandAuthenticatorService authenticatorService) {
        this.authenticatorService = authenticatorService;
    }

    /*
      This endpoint allows the Authenticator to update their profile details.
    */
    @PutMapping("/me")
    public ResponseEntity<String> updateProfile(@RequestBody AuthenticatorUpdateDTO updateDto) {
        // Grab the logged-in Authenticator from Spring Security
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LandAuthenticator currentAuthenticator = (LandAuthenticator) authentication.getPrincipal();

        // Pass the secure ID and the DTO to the service
        authenticatorService.updateAuthenticatorProfile(currentAuthenticator.getId(), updateDto);

        return new ResponseEntity<>("Verifier profile updated successfully", HttpStatus.OK);
    }

    // (Your teammate will eventually add the @PostMapping("/{listingId}/verify") here!)
}