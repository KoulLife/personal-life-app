package koul.PersonalApp.financial.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import koul.PersonalApp.global.entity.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class FinancialRecord extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long financialRecordId;

	private String description; // 소비 내역

	private Long amount; // 지출 금액

	String category; // 카테고리

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "financial_id")
	Financial financial;

	@Builder
	public FinancialRecord(String description, Long amount, String category, Financial financial) {
		this.description = description;
		this.amount = amount;
		this.category = category;
		this.financial = financial;

		financial.spend(amount);
	}

	/**
	 * 소비 내역 변경
	 */
	void updateDescription(String description) {
		this.description = description;
	}

	/**
	 * 지출 금액 변경
	 */
	void updateAmount(Long amount) {
		Long changedAmount = amount - this.amount;
		financial.spend(changedAmount);
	}

}
