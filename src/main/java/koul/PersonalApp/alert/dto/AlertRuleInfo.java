package koul.PersonalApp.alert.dto;

import koul.PersonalApp.alert.entity.ChannelType;
import koul.PersonalApp.user.entity.ServiceType;
import lombok.Builder;

/**
 * 알림 규칙 정보 응답 DTO
 */
@Builder
public record AlertRuleInfo(
        Long ruleId,
        ServiceType serviceType,
        Long channelId,
        ChannelType channelType, // 편의를 위해 채널 타입도 포함
        String channelName, // 채널 식별 정보 (이메일 주소, 웹훅 요약 등)
        Boolean enabled) {
}
