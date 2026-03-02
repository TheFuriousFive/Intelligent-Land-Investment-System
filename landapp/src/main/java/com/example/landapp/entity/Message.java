package com.example.landapp.entity;

import jakarta.persistence.*;
import lombok.Data;


@Entity

//set the name of the table on database manually
@Table(name = "messages")

@Inheritance(strategy = InheritanceType.JOINED)

//automatically generates all setters/getters, toSting, equals and hashCode
@Data

public abstract class Message {

    //make primary key of Message entity
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    //Linking to the LandListing entity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "land_listing_id")
    private LandListing landListing;
}
