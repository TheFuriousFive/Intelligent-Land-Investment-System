package com.example.landapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "investors")
@Data
@EqualsAndHashCode(callSuper = true)
public class Investor extends BaseUser {

    // Additional investor-specific fields could go here
    // e.g., investmentBudget or preferredRegions
}