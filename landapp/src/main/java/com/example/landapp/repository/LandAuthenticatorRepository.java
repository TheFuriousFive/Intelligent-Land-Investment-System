package com.example.landapp.repository;

import com.example.landapp.entity.LandAuthenticator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LandAuthenticatorRepository extends JpaRepository<LandAuthenticator, Long> {

    // Useful for logging in
    Optional<LandAuthenticator> findByUsername(String username);

    // Useful for checking if a professional is already registered
    Optional<LandAuthenticator> findByEmail(String email);

    Optional<LandAuthenticator> findByProfessionalRegNumber(String regNumber);
}