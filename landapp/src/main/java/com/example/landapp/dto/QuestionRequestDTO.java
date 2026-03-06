package com.example.landapp.dto;

import lombok.Data;

@Data

public class QuestionRequestDTO {
    private Long buyerId;    // Who is asking
    private Long landId;     // Which land are they asking about
    private String content;  // The question text
}
