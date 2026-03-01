package com.example.landapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Entity
@Table(name = "owners")
@Data
@EqualsAndHashCode(callSuper = true)
public class Owner extends BaseUser {

    private Double trustScore = 0.0;

    // Linking to the LandListing entity
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LandListing> landListings;


}