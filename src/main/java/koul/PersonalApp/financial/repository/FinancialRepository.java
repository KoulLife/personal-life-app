package koul.PersonalApp.financial.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import koul.PersonalApp.financial.entity.Financial;

public interface FinancialRepository extends JpaRepository<Financial, Long> {

	/**
	 * userId와 yearAndMonth로 Financial 조회
	 * yearAndMonth는 연-월만 비교 (일자 무시)
	 */
	@org.springframework.data.jpa.repository.Query("SELECT f FROM Financial f WHERE f.user.userId = :userId AND YEAR(f.yearAndMonth) = YEAR(:date) AND MONTH(f.yearAndMonth) = MONTH(:date)")
	java.util.Optional<Financial> findByUserIdAndYearMonth(
			@org.springframework.data.repository.query.Param("userId") Long userId,
			@org.springframework.data.repository.query.Param("date") java.time.LocalDate date);

	@org.springframework.data.jpa.repository.Query("SELECT f FROM Financial f WHERE f.user.userId = :userId AND f.yearAndMonth >= :startDate ORDER BY f.yearAndMonth ASC")
	java.util.List<Financial> findRecentFinancials(
			@org.springframework.data.repository.query.Param("userId") Long userId,
			@org.springframework.data.repository.query.Param("startDate") java.time.LocalDate startDate);
}
