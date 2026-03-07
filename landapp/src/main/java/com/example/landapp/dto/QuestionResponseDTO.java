package com.example.landapp.dto;

import lombok.Data;

import java.time.LocalDateTime;
@Data

public class QuestionResponseDTO {
    private Long id;
    private String content;
    private LocalDateTime createdAt;

    // Nested Answer DTO (null if not yet answered)
    private AnswerResponseDTO answer;
}
