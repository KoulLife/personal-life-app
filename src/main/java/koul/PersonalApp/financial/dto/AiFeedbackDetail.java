package koul.PersonalApp.financial.dto;

/**
 * AI 피드백 상세 항목
 * 카테고리별 분석 결과를 담는 DTO
 */
public record AiFeedbackDetail(
        String category, // 카테고리명 (예: "식비 지출", "고정비 관리")
        String content, // 분석 내용
        String type // good/warning/neutral/danger
) {
}
