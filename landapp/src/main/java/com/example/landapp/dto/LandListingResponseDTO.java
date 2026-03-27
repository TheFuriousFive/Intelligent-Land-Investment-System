package com.example.landapp.dto;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class LandListingResponseDTO  implements Serializable {

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



    // The professional registration number of the authenticator
    // Useful for the investor to verify the legitimacy of who validated the listing
    private String authenticatorRegNumber;

    private List<String> imageUrls;
    private List<String> deedDocumentUrls;
}
