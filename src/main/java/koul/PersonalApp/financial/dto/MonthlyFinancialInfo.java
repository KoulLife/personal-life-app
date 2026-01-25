package koul.PersonalApp.financial.dto;

import lombok.Builder;

@Builder
public record MonthlyFinancialInfo(
	Long monthlyRevenue,
	Long monthlyAvailableFunds,
	Long monthlyExpenses
) {
}
