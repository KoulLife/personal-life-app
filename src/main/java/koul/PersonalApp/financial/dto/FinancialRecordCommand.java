package koul.PersonalApp.financial.dto;

import java.time.LocalDate;

import koul.PersonalApp.financial.entity.Financial;

public record FinancialRecordCommand(
	String description,
	Long amount,
	String category,
	Financial financial) {
}
