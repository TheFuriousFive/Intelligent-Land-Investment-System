package com.example.landapp.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;
import java.math.BigDecimal;

@Entity
@Table(name = "land_listings")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class LandListing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Double area; // Extracted from "Area (Acres/Perches)"

    @Column(nullable = false)
    private String location;

    @Column(name = "land_type")
    private String landType;

    @Enumerated(EnumType.STRING)
    private ListingStatus status; // e.g., AVAILABLE, SOLD, PENDING

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "posted_date", updatable = false)
    private Date postedDate;

    // Relationship with the Owner entity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    @PrePersist
    protected void onCreate() {
        this.postedDate = new Date();
        if (this.status == null) {
            this.status = ListingStatus.AVAILABLE;
        }
    }
}
