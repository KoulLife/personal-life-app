package koul.PersonalApp.financial.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import koul.PersonalApp.financial.dto.AiFeedbackDetail;
import koul.PersonalApp.financial.dto.AiFinancialFeedback;
import koul.PersonalApp.financial.entity.Financial;
import koul.PersonalApp.financial.entity.FinancialRecord;
import koul.PersonalApp.financial.entity.FinancialUserProfile;
import koul.PersonalApp.financial.repository.FinancialRecordRepository;
import koul.PersonalApp.financial.repository.FinancialRepository;
import koul.PersonalApp.financial.repository.FinancialUserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * AI 재정 분석 서비스 구현체
 * Google Gemini를 활용한 재정 데이터 분석 및 피드백 생성
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FinancialAiServiceImpl implements FinancialAiService {

    private final ChatClient.Builder chatClientBuilder;
    private final FinancialRepository financialRepository;
    private final FinancialUserProfileRepository financialUserProfileRepository;
    private final FinancialRecordRepository financialRecordRepository;
    private final ObjectMapper objectMapper;

    @Value("${ai.feedback.max-records-per-request:100}")
    private int maxRecordsPerRequest;

    @Override
    public AiFinancialFeedback generateMonthlyFeedback(Long userId, LocalDate targetMonth) {
        log.info("AI 피드백 생성 시작 - userId: {}, targetMonth: {}", userId, targetMonth);

        // 1. 사용자 재정 프로필 조회
        FinancialUserProfile userProfile = financialUserProfileRepository.findByUser_UserId(userId)
                .orElse(null);

        // 2. 해당 월의 Financial 데이터 조회
        Financial financial = financialRepository.findByUserIdAndYearMonth(userId, targetMonth)
                .orElseThrow(() -> new IllegalStateException("해당 월의 재정 데이터가 없습니다."));

        // 3. 해당 월의 소비 내역 조회
        List<FinancialRecord> records = financial.getFinancialRecords();

        // 4. 프롬프트 구성
        String prompt = buildAnalysisPrompt(userProfile, financial, records, targetMonth);

        // 5. AI 호출
        String aiResponse = callGeminiAi(prompt);

        // 6. 응답 파싱
        return parseAiResponse(aiResponse);
    }

    @Override
    public String convertToJson(AiFinancialFeedback feedback) {
        try {
            return objectMapper.writeValueAsString(feedback);
        } catch (JsonProcessingException e) {
            log.error("AI 피드백 JSON 변환 실패", e);
            return "{\"error\": \"JSON 변환 실패\"}";
        }
    }

    /**
     * AI 분석 프롬프트 구성
     */
    private String buildAnalysisPrompt(FinancialUserProfile userProfile, Financial financial,
            List<FinancialRecord> records, LocalDate targetMonth) {

        StringBuilder prompt = new StringBuilder();
        prompt.append("당신은 전문 재무 컨설턴트입니다. 아래 사용자의 재정 데이터를 분석하고 유용한 피드백을 제공해주세요.\n\n");

        // 사용자 프로필 정보
        prompt.append("## 사용자 재정 프로필\n");
        if (userProfile != null && userProfile.getProfileData() != null) {
            prompt.append(userProfile.getProfileData()).append("\n\n");
        } else {
            prompt.append("프로필 정보 없음\n\n");
        }

        // 월간 요약 정보
        prompt.append("## ").append(targetMonth.getYear()).append("년 ").append(targetMonth.getMonthValue())
                .append("월 재정 요약\n");
        prompt.append("- 총 수익: ").append(formatCurrency(financial.getRevenue())).append("\n");
        prompt.append("- 가용 자금: ").append(formatCurrency(financial.getAvailableFunds())).append("\n");
        prompt.append("- 총 지출: ").append(formatCurrency(financial.getExpenses())).append("\n\n");

        // 카테고리별 지출 요약
        Map<String, Long> categorySpending = summarizeByCategory(records);
        if (!categorySpending.isEmpty()) {
            prompt.append("## 카테고리별 지출 내역\n");
            categorySpending.forEach((category, amount) -> {
                prompt.append("- ").append(category).append(": ").append(formatCurrency(amount)).append("\n");
            });
            prompt.append("\n");
        }

        // 주요 소비 내역 (상위 10개만)
        if (!records.isEmpty()) {
            prompt.append("## 주요 소비 내역 (상위 10개)\n");
            records.stream()
                    .sorted((r1, r2) -> r2.getAmount().compareTo(r1.getAmount()))
                    .limit(10)
                    .forEach(record -> {
                        prompt.append("- ").append(record.getDescription())
                                .append(": ").append(formatCurrency(record.getAmount()))
                                .append(" (").append(record.getCategory()).append(")\n");
                    });
            prompt.append("\n");
        }

        // 응답 형식 지시
        prompt.append("""
                ## 응답 형식
                아래 JSON 형식으로 정확하게 응답해주세요. JSON 외의 다른 텍스트는 포함하지 마세요:

                {
                  "score": <0-100 사이의 재무 건강 점수>,
                  "status": "<상태 텍스트: 예: 우수, 양호, 주의 필요, 개선 필요>",
                  "summary": "<2-3문장으로 이번 달 재정 상황을 요약>",
                  "details": [
                    {
                      "category": "<분석 카테고리명>",
                      "content": "<구체적인 분석 내용>",
                      "type": "<good/warning/neutral/danger 중 하나>"
                    }
                  ],
                  "actions": [
                    "<구체적이고 실행 가능한 추천 액션 1>",
                    "<구체적이고 실행 가능한 추천 액션 2>",
                    "<구체적이고 실행 가능한 추천 액션 3>"
                  ]
                }

                중요 지침:
                1. 점수는 수익 대비 지출 비율, 저축률, 지출 패턴 등을 종합적으로 고려하여 산정
                2. details는 최소 3개, 최대 5개 항목으로 구성
                3. actions는 실제로 실행 가능한 구체적인 조언 3-5개
                4. 모든 금액은 한국 원화 기준으로 분석
                5. 긍정적인 부분과 개선이 필요한 부분을 균형있게 제시
                6. 응답은 반드시 순수 JSON만 포함 (마크다운 코드 블록 없이)
                """);

        return prompt.toString();
    }

    /**
     * Google Gemini AI 호출
     */
    private String callGeminiAi(String prompt) {
        try {
            ChatClient chatClient = chatClientBuilder.build();

            String response = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

            log.debug("AI 응답: {}", response);
            return response;

        } catch (Exception e) {
            log.error("AI 호출 실패", e);
            // 폴백 응답 생성
            return generateFallbackResponse();
        }
    }

    /**
     * AI 응답 파싱
     */
    private AiFinancialFeedback parseAiResponse(String aiResponse) {
        try {
            // JSON 코드 블록 제거 (```json ... ``` 형식 처리)
            String cleanedResponse = aiResponse.trim();
            if (cleanedResponse.startsWith("```json")) {
                cleanedResponse = cleanedResponse.substring(7);
            }
            if (cleanedResponse.startsWith("```")) {
                cleanedResponse = cleanedResponse.substring(3);
            }
            if (cleanedResponse.endsWith("```")) {
                cleanedResponse = cleanedResponse.substring(0, cleanedResponse.length() - 3);
            }
            cleanedResponse = cleanedResponse.trim();

            // JSON 파싱
            return objectMapper.readValue(cleanedResponse, AiFinancialFeedback.class);

        } catch (Exception e) {
            log.error("AI 응답 파싱 실패: {}", aiResponse, e);
            // 파싱 실패 시 기본 응답 반환
            return createDefaultFeedback();
        }
    }

    /**
     * 카테고리별 지출 요약
     */
    private Map<String, Long> summarizeByCategory(List<FinancialRecord> records) {
        return records.stream()
                .limit(maxRecordsPerRequest)
                .collect(Collectors.groupingBy(
                        record -> record.getCategory() != null ? record.getCategory() : "기타",
                        Collectors.summingLong(FinancialRecord::getAmount)));
    }

    /**
     * 금액 포맷팅
     */
    private String formatCurrency(Long amount) {
        if (amount == null)
            return "0원";
        return String.format("%,d원", amount);
    }

    /**
     * AI 호출 실패 시 폴백 응답
     */
    private String generateFallbackResponse() {
        return """
                {
                  "score": 50,
                  "status": "분석 중",
                  "summary": "AI 분석을 처리하는 중입니다. 잠시 후 다시 확인해주세요.",
                  "details": [
                    {
                      "category": "시스템 알림",
                      "content": "현재 AI 분석 서비스에 일시적인 문제가 발생했습니다.",
                      "type": "warning"
                    }
                  ],
                  "actions": [
                    "잠시 후 다시 시도해주세요."
                  ]
                }
                """;
    }

    /**
     * 파싱 실패 시 기본 피드백
     */
    private AiFinancialFeedback createDefaultFeedback() {
        return new AiFinancialFeedback(
                50,
                "분석 중",
                "재정 데이터를 분석하고 있습니다.",
                List.of(new AiFeedbackDetail(
                        "시스템 알림",
                        "AI 분석 결과를 처리하는 중 문제가 발생했습니다.",
                        "warning")),
                List.of("잠시 후 다시 확인해주세요."));
    }
}
