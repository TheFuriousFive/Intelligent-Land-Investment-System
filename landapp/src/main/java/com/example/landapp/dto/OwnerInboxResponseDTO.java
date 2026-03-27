package com.example.landapp.dto;

import com.example.landapp.entity.InquiryStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OwnerInboxResponseDTO {
    private Long inquiryId;
    private Long landListingId;
    private String landTitle;
    private String customMessage;

    // Updated to Full Name
    private String investorFullName;
    private String investorContactNumber;
    private String investorEmail;

    private InquiryStatus status;
    private LocalDateTime createdAt;
}