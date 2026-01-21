package koul.PersonalApp.alert.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import koul.PersonalApp.alert.dto.EventPayload;
import koul.PersonalApp.alert.service.AlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 알림 이벤트 수신 컨트롤러
 * 다른 서비스(Project Manager, Financial Manager 등)에서 발생한 이벤트를 받아 처리
 */
@Slf4j
@RestController
@RequestMapping("/alert/events")
@RequiredArgsConstructor
public class AlertEventController {

    private final AlertService alertService;

    /**
     * 이벤트 수신 엔드포인트
     * 다른 서비스에서 POST 요청으로 이벤트를 전달하면,
     * 비동기로 처리한 후 즉시 응답을 반환
     */
    @PostMapping
    public ResponseEntity<String> receiveEvent(@RequestBody @Valid EventPayload event) {
        log.info("이벤트 수신 - 서비스: {}, 타입: {}, 사용자: {}",
                event.serviceName(), event.eventType(), event.userId());

        // 비동기로 처리 (즉시 응답)
        alertService.processEventAsync(event);

        return ResponseEntity.ok("이벤트가 성공적으로 수신되었습니다.");
    }
}
