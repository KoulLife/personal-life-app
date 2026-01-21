package koul.PersonalApp.alert.dto;

import koul.PersonalApp.user.entity.ServiceType;
import lombok.Builder;

/**
 * 알림 규칙 생성 요청 DTO
 */
@Builder
public record AlertRuleCommand(
        ServiceType serviceType, // 어떤 서비스의 알림인지
        Long channelId, // 어떤 채널로 받을지
        Boolean enabled // 활성화 여부
) {
}
