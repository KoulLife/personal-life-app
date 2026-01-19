package koul.PersonalApp.financial.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import koul.PersonalApp.financial.scheduler.AiFeedbackScheduler;
import lombok.RequiredArgsConstructor;

/**
 * AI 피드백 배치 작업 테스트용 컨트롤러
 * 개발 환경에서만 사용하도록 주의
 */
@RestController
@RequestMapping("/admin/batch")
@RequiredArgsConstructor
public class AiFeedbackBatchController {

    private final AiFeedbackScheduler aiFeedbackScheduler;

    /**
     * AI 피드백 배치 작업 수동 실행
     * 테스트용 엔드포인트
     */
    @PostMapping("/ai-feedback/run")
    public ResponseEntity<String> runAiFeedbackBatch() {
        aiFeedbackScheduler.runManually();
        return ResponseEntity.ok("AI 피드백 배치 작업이 시작되었습니다. 로그를 확인해주세요.");
    }
}
