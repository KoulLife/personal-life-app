package koul.PersonalApp.financial.dto;

import java.util.List;

/**
 * AI 재정 분석 피드백 응답
 * AI가 생성한 월간 재정 건강 리포트를 담는 DTO
 */
public record AiFinancialFeedback(
        int score, // 재무 건강 점수 (0-100)
        String status, // 상태 텍스트 (예: "양호", "주의 필요")
        String summary, // 전반적인 요약
        List<AiFeedbackDetail> details, // 세부 분석 항목들
        List<String> actions // AI 추천 액션 리스트
) {
}
