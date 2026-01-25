package koul.PersonalApp.financial.dto;

import lombok.Builder;

@Builder
public record FinancialRecordCommand(
	String description,
	Long amount,
	String category
	) {
}
