package com.example.landapp.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AnswerResponseDTO {
    private Long id;
    private Long landListingId;
    private String content;
    private LocalDateTime answeredAt;
}
