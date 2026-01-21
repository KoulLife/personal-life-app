package koul.PersonalApp.alert.notifier;

import koul.PersonalApp.user.entity.ServiceType;

/**
 * 알림 전송 인터페이스
 * 다른 서비스(Project Manager, Financial Manager 등)에서
 * Alert Manager로 이벤트를 전송하기 위한 통합 인터페이스
 */
public interface AlertNotifier {

    /**
     * 특정 사용자에게 알림 전송
     * 
     * @param userId      알림을 받을 사용자 ID
     * @param serviceName 이벤트를 발생시킨 서비스
     * @param eventType   이벤트 타입
     * @param message     알림 메시지
     */
    void notifyUser(Long userId, ServiceType serviceName, String eventType, String message);

    /**
     * 특정 사용자에게 추가 데이터와 함께 알림 전송
     * 
     * @param userId      알림을 받을 사용자 ID
     * @param serviceName 이벤트를 발생시킨 서비스
     * @param eventType   이벤트 타입
     * @param message     알림 메시지
     * @param data        추가 데이터 (JSON 형태)
     */
    void notifyUserWithData(Long userId, ServiceType serviceName, String eventType, String message, String data);
}
