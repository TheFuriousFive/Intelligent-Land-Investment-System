package com.example.landapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Entity
@Table(name = "owners")
@Data
@EqualsAndHashCode(callSuper = true) // Tells Lombok to include parent fields in comparisons
public class Owner extends BaseUser {

    private String nicNumber;
    private String address;
    private String phoneNumber;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<LandListing> landListings;
}
