package koul.PersonalApp.alert.dto;

import koul.PersonalApp.alert.entity.ChannelType;
import lombok.Builder;

/**
 * 알림 채널 등록/수정 요청 DTO
 */
@Builder
public record NotificationChannelCommand(
        ChannelType channelType,
        Boolean enabled,
        String channelConfig // JSON 문자열
) {
}
