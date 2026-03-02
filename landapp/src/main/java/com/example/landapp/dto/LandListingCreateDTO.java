package com.example.landapp.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LandListingDTO {
    private Long id;
    private String title;
    private String location;
    private BigDecimal price;
    private Double area;
    private String landType;
    private String status;

    // Instead of the whole Owner object, we just send the name
    private String ownerName;
}