package com.example.landapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AnswerRequestDTO {

    @NotNull(message = "Owner ID is required to verify authorization")
    private Long ownerId;

    @NotNull(message = "Land ID is required to verify ")
    private Long landlistingId;

    @NotBlank(message = "Answer content cannot be empty")
    private String content;
}
