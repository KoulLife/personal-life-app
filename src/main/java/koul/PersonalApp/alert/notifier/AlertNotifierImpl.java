package koul.PersonalApp.alert.notifier;

import org.springframework.stereotype.Component;

import koul.PersonalApp.alert.dto.EventPayload;
import koul.PersonalApp.alert.service.AlertService;
import koul.PersonalApp.user.entity.ServiceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * AlertNotifier 구현체
 * HTTP API 호출 대신 직접 AlertService를 호출하여 알림 전송
 * (같은 애플리케이션 내에서 실행되므로 내부 메서드 호출 방식 사용)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AlertNotifierImpl implements AlertNotifier {

    private final AlertService alertService;

    /**
     * 특정 사용자에게 알림 전송
     */
    @Override
    public void notifyUser(Long userId, ServiceType serviceName, String eventType, String message) {
        notifyUserWithData(userId, serviceName, eventType, message, null);
    }

    /**
     * 특정 사용자에게 추가 데이터와 함께 알림 전송
     */
    @Override
    public void notifyUserWithData(Long userId, ServiceType serviceName, String eventType, String message,
            String data) {
        log.info("알림 전송 요청 - 사용자: {}, 서비스: {}, 이벤트: {}", userId, serviceName, eventType);

        EventPayload event = EventPayload.builder()
                .userId(userId)
                .serviceName(serviceName)
                .eventType(eventType)
                .message(message)
                .data(data)
                .build();

        // AlertService를 통해 비동기로 알림 발송
        alertService.processEventAsync(event);
    }
}
