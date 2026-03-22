package com.example.landapp.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReviewResponseDTO {
    private Long id;
    private int rating;
    private String comment;
    private String investorName;
    private LocalDateTime createdAt;
}