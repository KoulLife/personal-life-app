package koul.PersonalApp.financial.dto;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record FinancialRecordInfo(
        Long financialRecordId,
        String description,
        Long amount,
        String category,
        LocalDateTime createdDate) {
}
