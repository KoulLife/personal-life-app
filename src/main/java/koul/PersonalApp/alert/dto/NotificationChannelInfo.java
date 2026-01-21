package koul.PersonalApp.alert.dto;

import koul.PersonalApp.alert.entity.ChannelType;
import lombok.Builder;

/**
 * 알림 채널 정보 응답 DTO
 */
@Builder
public record NotificationChannelInfo(
        Long channelId,
        ChannelType channelType,
        Boolean enabled,
        String channelConfig // JSON 문자열
) {
}
