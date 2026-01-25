package koul.PersonalApp.financial.service;

import java.util.List;
import java.util.Map;

import koul.PersonalApp.financial.dto.FinancialRecordCommand;
import koul.PersonalApp.financial.dto.FinancialRecordInfo;
import koul.PersonalApp.financial.dto.MonthlyFinancialInfo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FinancialService {

	/**
	 * 금융 대시보드 요약 정보 반환 (총 수익, 가용 자금, 총 지출)
	 */
	MonthlyFinancialInfo getFinancialSummary(Long userId);

	/**
	 * 재정 상태 등록 (초기 설정)
	 */
	void registerFinancialStatus(Long userId, MonthlyFinancialInfo command);

	/**
	 * 재정 상태 수정
	 */
	void updateFinancialStatus(Long userId, MonthlyFinancialInfo command);

	/**
	 * 소비/수입 내역 추가
	 */
	void addFinanceRecord(Long userId, FinancialRecordCommand command);

	/**
	 * 최근 소비 내역 조회 (페이징)
	 */
	Page<FinancialRecordInfo> getRecentFinanceRecord(Long userId, Pageable pageable);

	/**
	 * 월별 지출 히트맵 데이터 조회 (일별 지출 금액)
	 */
	Map<Integer, Long> getMonthlyExpenseHeatmap(Long userId, int year, int month);

	/**
	 * AI 리포트 반환
	 */
	String getAIReport(Long userId);

	/**
	 * 주기적인 AI 리포트 데이터 삽입
	 */
	void upsertPeriodicAiReport(Long userId, String report);

	/**
	 * 재정 그래프 데이터 조회 (월별 추세)
	 */
	Map<String, List<Long>> getFinancialGraphData(Long userId);

}
