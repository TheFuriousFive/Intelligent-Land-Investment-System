package com.example.landapp.service;

import org.springframework.stereotype.Component;
import java.security.SecureRandom;

@Component
public class RicGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String CHARS = "ABCDEF0123456789";

    // Generates: RIC-7F3A-9B2C-1A4D
    public String generate() {
        return "RIC-" + seg() + "-" + seg() + "-" + seg();
    }

    private String seg() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            sb.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
        }
        return sb.toString();
    }
}