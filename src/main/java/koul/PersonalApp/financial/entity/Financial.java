package koul.PersonalApp.financial.entity;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import koul.PersonalApp.financial.dto.MonthlyFinancialInfo;
import koul.PersonalApp.global.entity.BaseTimeEntity;
import koul.PersonalApp.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Financial extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long financialId;

	private Long revenue; // 총 수익

	private Long availableFunds; // 가용 자금

	private Long expenses; // 총 지출

	private LocalDate yearAndMonth; // yyyy-mm

	@Column(columnDefinition = "TEXT")
	private String aiFeedback; // AI 월간 피드백

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	User user; // 유저

	@OneToMany(mappedBy = "financial", cascade = CascadeType.ALL)
	List<FinancialRecord> financialRecords;

	@Builder
	public Financial(Long revenue, Long availableFunds, Long expenses, LocalDate yearAndMonth, User user, List<FinancialRecord> financialRecords) {
		this.revenue = revenue;
		this.availableFunds = availableFunds;
		this.expenses = expenses;
		this.yearAndMonth = yearAndMonth;
		this.user = user;
		this.financialRecords = financialRecords;
	}

	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * 월간 총 수익, 가용 자금, 총 지출 정보 반환
	 */
	public MonthlyFinancialInfo getMonthlyFinancialInfo() {
		return MonthlyFinancialInfo.builder()
				.monthlyRevenue(revenue)
				.monthlyAvailableFunds(availableFunds)
				.monthlyExpenses(expenses)
				.build();
	}

	/**
	 * 총 수익 재설정
	 */
	public void updateRevenue(Long revenue) {
		this.revenue = revenue;
	}

	/**
	 * 가용 자금 재설정
	 */
	public void updateAvailableFunds(Long availableFunds) {
		this.availableFunds = availableFunds;
	}

	/**
	 * 총 지출 재설정
	 */
	public void updateExpenses(Long expenses) {
		this.expenses = expenses;
	}

	/**
	 * 지출 발생
	 */
	public void spend(Long amount) {
		this.expenses += amount;
	}

	/**
	 * AI 월간 리포트 내용 작성
	 */
	public void updateAiFeedback(String aiFeedback) {
		this.aiFeedback = aiFeedback;
	}

}
