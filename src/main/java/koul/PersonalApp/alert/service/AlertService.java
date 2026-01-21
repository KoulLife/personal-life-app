package koul.PersonalApp.alert.service;

import koul.PersonalApp.alert.dto.EventPayload;
import koul.PersonalApp.user.entity.ServiceType;

public interface AlertService {

    /**
     * 이벤트를 비동기로 처리하여 알림 발송
     * 이벤트를 받으면 사용자에게 설정된 활성화된 채널로 알림을 보냄
     */
    void processEventAsync(EventPayload event);

    /**
     * 특정 사용자에게 메시지 전송 (모든 채널)
     * 
     * @param userId  알림을 받을 사용자 ID
     * @param message 알림 메시지
     */
    void sendNotification(Long userId, String message);

    /**
     * 특정 사용자에게 서비스별 규칙을 적용하여 알림 전송
     * 
     * @param userId      알림을 받을 사용자 ID
     * @param serviceType 알림을 발송하는 서비스 타입
     * @param message     알림 메시지
     */
    void sendNotification(Long userId, ServiceType serviceType, String message);
}
