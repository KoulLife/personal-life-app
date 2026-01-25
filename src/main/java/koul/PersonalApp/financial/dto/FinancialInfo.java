package koul.PersonalApp.financial.dto;

public record FinancialInfo(
        Long currentTotalAssets,
        Long monthlyBudget,
        Long monthlyIncome,
        Long currentMonthExpense,
        Long currentMonthIncome,
        Long availableFunds // Budget - Expense
) {
}
