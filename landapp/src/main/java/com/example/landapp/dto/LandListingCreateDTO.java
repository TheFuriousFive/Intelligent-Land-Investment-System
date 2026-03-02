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

}
