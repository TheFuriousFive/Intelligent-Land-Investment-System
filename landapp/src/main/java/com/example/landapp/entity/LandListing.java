package com.example.landapp.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;
import java.math.BigDecimal;
import java.util.List;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false)
    private VerificationStatus verificationStatus;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "posted_date", updatable = false)
    private Date postedDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "verified_at")
    private Date verifiedAt;

    // Relationship with the Owner entity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    // nullable = true because when a listing is first created, no authenticator has been
    // assigned yet. An authenticator is only linked AFTER they review the documents.
    // This also means if an authenticator account is deleted, this becomes NULL safely
    // and the listing is NOT deleted (avoids the CascadeType.REMOVE problem).
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authenticator_id", nullable = true)
    private LandAuthenticator landAuthenticator;

    //Overpass inputs
    @Column(name= "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    // New fields for Overpass data
    @Column(name = "osm_land_use")
    private String osmLandUse; // e.g., residential, meadow, forest

    @Column(name = "osm_amenities", columnDefinition = "TEXT")
    private String osmAmenities; // comma-separated list of nearby features

    @Column(name = "osm_access_road")
    private String osmAccessRoad; // type of road leading to land

    // --- NEW FIELDS FOR SUPABASE URLS ---

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "land_listing_images", joinColumns = @JoinColumn(name = "listing_id"))
    @Column(name = "image_url", nullable = false)
    private List<String> imageUrls;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "land_listing_documents", joinColumns = @JoinColumn(name = "listing_id"))
    @Column(name = "document_url", nullable = false)
    private List<String> deedDocumentUrls;

    @PrePersist
    protected void onCreate() {
        this.postedDate = new Date();
        if (this.status == null) {
            this.status = ListingStatus.AVAILABLE;
        }
        // Every listing starts as PENDING_VERIFICATION when first created
        // Matches your State Machine: Owner Creates Listing → Draft → Upload Docs → Pending_Verification
        if (this.verificationStatus == null) {
            this.verificationStatus = VerificationStatus.PENDING_VERIFICATION;
        }
    }
}
