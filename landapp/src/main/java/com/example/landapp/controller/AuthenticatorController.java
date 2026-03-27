package com.example.landapp.controller;

import com.example.landapp.dto.AuthenticationDecisionDTO;
import com.example.landapp.service.LandAuthenticatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/authenticator")
public class AuthenticatorController {

    @Autowired
    private LandAuthenticatorService authenticatorService;

    // Submit approval or rejection
    @PostMapping("/verify-land")
    public ResponseEntity<String> verifyLand(@RequestBody AuthenticationDecisionDTO decision) {
        authenticatorService.authenticateLand(decision);
        return ResponseEntity.ok("Verification status updated to: " + (decision.isApproved() ? "APPROVED" : "REJECTED"));
    }
}
