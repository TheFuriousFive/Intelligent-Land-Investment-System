package com.example.landapp.repository;

import com.example.landapp.entity.BaseUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<BaseUser, Long> {
    // This method will now search the 'users' table
    // and return the specific subclass (Owner/Investor/etc.)
    Optional<BaseUser> findByEmail(String email);
}