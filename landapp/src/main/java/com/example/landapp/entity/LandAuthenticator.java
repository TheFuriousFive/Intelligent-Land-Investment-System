package com.example.landapp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "land_authenticators")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class LandAuthenticator extends BaseUser { // Extend BaseUser

    @Column(name = "professional_reg_number", nullable = false, unique = true)
    private String professionalRegNumber;

    private BigDecimal walletBalance =BigDecimal.ZERO ;
    // You can add a method or mapping here to link them to the listings they authenticate
}
