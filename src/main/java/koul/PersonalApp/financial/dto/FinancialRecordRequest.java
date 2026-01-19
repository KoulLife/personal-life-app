package koul.PersonalApp.financial.dto;

import lombok.Builder;

@Builder
public record FinancialRecordRequest(
	String description,
	Long amount,
	String category
) {
}
