package com.example.landapp.dto;
import lombok.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class LandListingResponseDTO {

    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private Double area;
    private String location;
    private String landType;
    private String status;
    private Date postedDate;

    // Frontend can see only owner name
    private String ownerName;

    // Frontend needs to know the verification state of the listing
    private String verificationStatus;

    //  When was this listing verified (null if still pending or rejected)
    private Date verifiedAt;

    // (i.e., verificationStatus is still PENDING_VERIFICATION)
    // This allows the frontend to show "Not yet reviewed" when it's null
    private String authenticatorName;

    // The professional registration number of the authenticator
    // Useful for the investor to verify the legitimacy of who validated the listing
    private String authenticatorRegNumber;
}
