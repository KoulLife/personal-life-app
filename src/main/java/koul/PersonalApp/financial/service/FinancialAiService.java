package koul.PersonalApp.financial.service;

import java.time.LocalDate;

import koul.PersonalApp.financial.dto.AiFinancialFeedback;

/**
 * AI 재정 분석 서비스
 * Google Gemini를 활용한 재정 데이터 분석 및 피드백 생성
 */
public interface FinancialAiService {

    /**
     * 월간 AI 피드백 생성
     * 사용자의 재정 프로필과 소비 내역을 분석하여 AI 피드백 생성
     *
     * @param userId      사용자 ID
     * @param targetMonth 분석 대상 월 (yyyy-MM-01 형식)
     * @return AI 피드백 객체
     */
    AiFinancialFeedback generateMonthlyFeedback(Long userId, LocalDate targetMonth);

    /**
     * AI 피드백을 JSON 문자열로 변환
     *
     * @param feedback AI 피드백 객체
     * @return JSON 문자열
     */
    String convertToJson(AiFinancialFeedback feedback);
}
