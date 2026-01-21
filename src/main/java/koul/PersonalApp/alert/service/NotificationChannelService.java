package koul.PersonalApp.alert.service;

import java.util.List;

import koul.PersonalApp.alert.dto.NotificationChannelCommand;
import koul.PersonalApp.alert.dto.NotificationChannelInfo;
import koul.PersonalApp.alert.entity.ChannelType;

public interface NotificationChannelService {

    /**
     * 사용자의 모든 알림 채널 조회
     */
    List<NotificationChannelInfo> getAllChannels(Long userId);

    /**
     * 사용자의 활성화된 알림 채널만 조회
     */
    List<NotificationChannelInfo> getEnabledChannels(Long userId);

    /**
     * 특정 타입의 알림 채널 조회
     */
    NotificationChannelInfo getChannelByType(Long userId, ChannelType channelType);

    /**
     * 알림 채널 등록
     */
    void registerChannel(Long userId, NotificationChannelCommand command);

    /**
     * 알림 채널 설정 수정
     */
    void updateChannel(Long userId, Long channelId, NotificationChannelCommand command);

    /**
     * 알림 채널 삭제
     */
    void deleteChannel(Long userId, Long channelId);

    /**
     * 알림 채널 활성화
     */
    void enableChannel(Long userId, Long channelId);

    /**
     * 알림 채널 비활성화
     */
    void disableChannel(Long userId, Long channelId);
}
