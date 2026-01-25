package koul.PersonalApp.financial.scheduler;

import java.time.LocalDateTime;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * AI 피드백 스케줄러
 * 매월 마지막 날 23:59에 배치 작업 실행
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AiFeedbackScheduler {

    private final JobLauncher jobLauncher;
    private final Job monthlyAiFeedbackJob;

    @Value("${ai.feedback.enabled:true}")
    private boolean feedbackEnabled;

    /**
     * 매월 마지막 날 23:59에 실행
     * cron: "0 59 23 L * ?" = 초 분 시 일 월 요일
     * L = Last day of month
     */
    @Scheduled(cron = "0 59 23 L * ?")
    public void runMonthlyFeedbackGeneration() {
        if (!feedbackEnabled) {
            log.info("AI 피드백 생성이 비활성화 되어 있습니다.");
            return;
        }

        try {
            log.info("=== AI 피드백 배치 작업 시작 ===");

            JobParameters jobParameters = new JobParametersBuilder()
                    .addLocalDateTime("executionTime", LocalDateTime.now())
                    .toJobParameters();

            jobLauncher.run(monthlyAiFeedbackJob, jobParameters);

            log.info("=== AI 피드백 배치 작업 완료 ===");

        } catch (Exception e) {
            log.error("AI 피드백 배치 작업 실행 중 오류 발생", e);
        }
    }

    /**
     * 테스트용 수동 실행 메서드
     * 개발 중 즉시 실행하고 싶을 때 사용
     */
    public void runManually() {
        log.info("=== AI 피드백 배치 작업 수동 실행 ===");
        runMonthlyFeedbackGeneration();
    }
}
