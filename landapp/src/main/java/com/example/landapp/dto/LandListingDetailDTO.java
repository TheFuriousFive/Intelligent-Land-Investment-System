package com.example.landapp.dto;

import com.example.landapp.entity.VerificationStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class LandListingDetailDTO {
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private Double area;
    private String location;
    private String landType;
    private VerificationStatus verificationStatus;
    private List<String> imageUrls;
    private List<String> deedDocumentUrls; // url for deed documents
    private String ownerName;
    private Double latitude;
    private Double longitude;
}
