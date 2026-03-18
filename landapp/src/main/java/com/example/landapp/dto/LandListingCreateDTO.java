package com.example.landapp.dto;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Data
public class LandListingCreateDTO {

    @NotBlank
    @Size(max = 100)
    private String title;

    private String description;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal price;

    @NotNull
    @Positive
    private Double area;

    @NotBlank
    private String location;

    private String landType;

    @NotNull
    private Long ownerId;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    // NOTE: We do NOT include authenticatorId or verificationStatus here.
    // Reason: When an Owner creates a listing, they don't choose who verifies it.
    // The verificationStatus is automatically set to PENDING_VERIFICATION by @PrePersist.
    // The authenticatorId is assigned later by the system when a LandAuthenticator
    // picks up the listing to review it — that is a separate API call/flow entirely.


}
