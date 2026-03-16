package com.example.landapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor //generates a constructor that required one argument for every field
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private Integer rating;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @CreationTimestamp
    @Column(updatable = false,nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "investorId", nullable = false)
    private Investor investor;

    @ManyToOne
    @JoinColumn(name = "ownerId", nullable = false)
    private Owner owner;
}
