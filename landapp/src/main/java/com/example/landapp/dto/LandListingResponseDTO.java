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

    // Flattened Owner details for the frontend
    // Frontend can see only owner name
    private String ownerName;
}
