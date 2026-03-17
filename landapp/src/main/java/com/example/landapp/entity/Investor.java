package com.example.landapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "investors")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Investor extends BaseUser {

    // Additional investor-specific fields could go here
    // e.g., investmentBudget or preferredRegions
    private String preferredLocation;   // what areas they're interested in
    private BigDecimal investmentBudget;    // budget range

    // relationships
    @OneToMany(mappedBy = "investor")
    private List<Question> questions;

    @OneToMany(mappedBy = "investor")
    private List<Review> reviews;
}
