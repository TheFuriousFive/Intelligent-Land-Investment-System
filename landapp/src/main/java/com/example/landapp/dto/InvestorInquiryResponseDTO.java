package com.example.landapp.dto;

import com.example.landapp.entity.InquiryStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class InvestorInquiryResponseDTO {
    private Long inquiryId;
    private String landTitle;
    private String ownerFullName;
    private InquiryStatus status;

    // This is the magic field that updates based on the Owner's action!
    private String platformUpdateMessage;

    private LocalDateTime createdAt;
}
