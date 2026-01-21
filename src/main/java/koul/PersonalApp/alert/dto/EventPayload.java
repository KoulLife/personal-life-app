package koul.PersonalApp.alert.dto;

import koul.PersonalApp.user.entity.ServiceType;
import lombok.Builder;

/**
 * 알림 이벤트 페이로드
 * 다른 서비스에서 Alert Manager로 전달되는 이벤트 데이터
 */
@Builder
public record EventPayload(
        ServiceType serviceName, // 이벤트를 발생시킨 서비스 (PROJECT_MANAGER, FINANCIAL_MANAGER 등)
        String eventType, // 이벤트 타입 (TASK_COMPLETED, BUDGET_EXCEEDED 등)
        Long userId, // 알림을 받을 사용자 ID
        String message, // 알림 메시지 내용
        String data // 추가 데이터 (JSON 형태)
) {
}
