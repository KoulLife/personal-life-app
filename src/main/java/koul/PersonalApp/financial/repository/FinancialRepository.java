package koul.PersonalApp.financial.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import koul.PersonalApp.financial.entity.Financial;

public interface FinancialRepository extends JpaRepository<Financial, Long> {

	/**
	 * userId와 yearAndMonth로 Financial 조회
	 * yearAndMonth는 연-월만 비교 (일자 무시)
	 * PostgreSQL의 date_trunc 함수를 사용하여 월 단위로 비교
	 */
	@Query("SELECT f FROM Financial f WHERE f.user.userId = :userId " +
			"AND FUNCTION('date_trunc', 'month', f.yearAndMonth) = FUNCTION('date_trunc', 'month', CAST(:date AS date))")
	Optional<Financial> findByUserIdAndYearMonth(
			@Param("userId") Long userId,
			@Param("date") LocalDate date);

	@Query("SELECT f FROM Financial f WHERE f.user.userId = :userId AND f.yearAndMonth >= :startDate ORDER BY f.yearAndMonth ASC")
	List<Financial> findRecentFinancials(
			@Param("userId") Long userId,
			@Param("startDate") LocalDate startDate);

	/**
	 * AI 피드백이 없는 사용자 ID 조회
	 * 배치 작업에서 AI 피드백을 생성할 대상 사용자 목록 조회
	 */
	@Query("SELECT DISTINCT f.user.userId FROM Financial f " +
			"WHERE FUNCTION('date_trunc', 'month', f.yearAndMonth) = FUNCTION('date_trunc', 'month', CAST(:targetMonth AS date)) "
			+
			"AND (f.aiFeedback IS NULL OR f.aiFeedback = '')")
	List<Long> findUserIdsWithoutFeedback(@Param("targetMonth") LocalDate targetMonth);
}
