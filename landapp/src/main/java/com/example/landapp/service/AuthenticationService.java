package com.example.landapp.service;

import com.example.landapp.dto.InvestorRegistrationDTO;
import com.example.landapp.dto.LoginDTO;
import com.example.landapp.dto.OwnerRegistrationDTO;
import com.example.landapp.entity.BaseUser;
import com.example.landapp.entity.Investor;
import com.example.landapp.entity.Owner;
import com.example.landapp.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    // --- REGISTRATION METHODS ---

    public Owner registerOwner(OwnerRegistrationDTO input) {
        Owner owner = new Owner();
        owner.setFirstName(input.getFirstName());
        owner.setLastName(input.getLastName());
        owner.setEmail(input.getEmail());
        owner.setContactNumber(input.getContactNumber());

        // Hash the password before saving!
        owner.setPasswordHash(passwordEncoder.encode(input.getPassword()));

        return userRepository.save(owner);
    }

    public Investor registerInvestor(InvestorRegistrationDTO input) {
        Investor investor = new Investor();
        investor.setFirstName(input.getFirstName());
        investor.setLastName(input.getLastName());
        investor.setEmail(input.getEmail());
        investor.setContactNumber(input.getContactNumber());

        // Hash the password before saving!
        investor.setPasswordHash(passwordEncoder.encode(input.getPassword()));

        return userRepository.save(investor);
    }

    // --- AUTHENTICATION (LOGIN) METHOD ---

    public BaseUser authenticate(LoginDTO input) {
        // 1. Spring Security checks if the email and raw password match the hash in the DB
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        // 2. If successful, fetch the user from the database and return them
        return userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found after authentication"));
    }
}