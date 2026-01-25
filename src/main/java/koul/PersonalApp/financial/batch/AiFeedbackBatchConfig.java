package koul.PersonalApp.financial.batch;

import java.time.LocalDate;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import koul.PersonalApp.financial.dto.AiFinancialFeedback;
import koul.PersonalApp.financial.repository.FinancialRepository;
import koul.PersonalApp.financial.service.FinancialAiService;
import koul.PersonalApp.financial.service.FinancialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * AI 피드백 배치 작업 설정
 * 매월 말일에 모든 사용자의 재정 데이터를 분석하여 AI 피드백 생성
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class AiFeedbackBatchConfig {

    private final FinancialRepository financialRepository;
    private final FinancialAiService financialAiService;
    private final FinancialService financialService;

    /**
     * AI 피드백 생성 Job
     */
    @Bean
    public Job monthlyAiFeedbackJob(JobRepository jobRepository, Step generateFeedbackStep) {
        return new JobBuilder("monthlyAiFeedbackJob", jobRepository)
                .start(generateFeedbackStep)
                .build();
    }

    /**
     * AI 피드백 생성 Step
     */
    @Bean
    public Step generateFeedbackStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager) {

        return new StepBuilder("generateFeedbackStep", jobRepository)
                .<Long, String>chunk(10, transactionManager) // 10명씩 묶어서 처리
                .reader(userItemReader())
                .processor(aiFeedbackProcessor())
                .writer(aiFeedbackWriter())
                .build();
    }

    /**
     * ItemReader: AI 피드백이 필요한 사용자 목록 조회
     */
    @Bean
    public ListItemReader<Long> userItemReader() {
        LocalDate currentMonth = LocalDate.now().withDayOfMonth(1);
        List<Long> userIds = financialRepository.findUserIdsWithoutFeedback(currentMonth);

        log.info("AI 피드백 생성 대상 사용자 수: {}", userIds.size());

        return new ListItemReader<>(userIds);
    }

    /**
     * ItemProcessor: 각 사용자에 대해 AI 피드백 생성
     */
    @Bean
    public ItemProcessor<Long, String> aiFeedbackProcessor() {
        return userId -> {
            try {
                log.info("AI 피드백 생성 중 - userId: {}", userId);

                LocalDate targetMonth = LocalDate.now().withDayOfMonth(1);
                AiFinancialFeedback feedback = financialAiService.generateMonthlyFeedback(userId, targetMonth);

                String jsonFeedback = financialAiService.convertToJson(feedback);
                log.info("AI 피드백 생성 완료 - userId: {}, score: {}", userId, feedback.score());

                // userId와 feedback을 함께 전달하기 위해 특수 포맷 사용
                return userId + "|" + jsonFeedback;

            } catch (Exception e) {
                log.error("AI 피드백 생성 실패 - userId: {}", userId, e);
                // 실패한 경우 null 반환하여 writer에서 스킵
                return null;
            }
        };
    }

    /**
     * ItemWriter: 생성된 AI 피드백을 DB에 저장
     */
    @Bean
    public ItemWriter<String> aiFeedbackWriter() {
        return chunk -> {
            for (String item : chunk.getItems()) {
                if (item == null)
                    continue;

                // userId|jsonFeedback 형식 파싱
                String[] parts = item.split("\\|", 2);
                if (parts.length != 2)
                    continue;

                Long userId = Long.parseLong(parts[0]);
                String jsonFeedback = parts[1];

                try {
                    financialService.upsertPeriodicAiReport(userId, jsonFeedback);
                    log.info("AI 피드백 저장 완료 - userId: {}", userId);
                } catch (Exception e) {
                    log.error("AI 피드백 저장 실패 - userId: {}", userId, e);
                }
            }
        };
    }
}
