package koul.PersonalApp.alert.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import koul.PersonalApp.alert.dto.NotificationChannelCommand;
import koul.PersonalApp.alert.dto.NotificationChannelInfo;
import koul.PersonalApp.alert.entity.ChannelType;
import koul.PersonalApp.alert.entity.NotificationChannel;
import koul.PersonalApp.alert.repository.NotificationChannelRepository;
import koul.PersonalApp.user.entity.User;
import koul.PersonalApp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationChannelServiceImpl implements NotificationChannelService {

    private final NotificationChannelRepository channelRepository;
    private final UserRepository userRepository;

    /**
     * 사용자의 모든 알림 채널 조회
     */
    @Override
    public List<NotificationChannelInfo> getAllChannels(Long userId) {
        return channelRepository.findByUser_UserId(userId).stream()
                .map(this::toInfo)
                .collect(Collectors.toList());
    }

    /**
     * 사용자의 활성화된 알림 채널만 조회
     */
    @Override
    public List<NotificationChannelInfo> getEnabledChannels(Long userId) {
        return channelRepository.findByUserIdAndEnabledTrue(userId).stream()
                .map(this::toInfo)
                .collect(Collectors.toList());
    }

    /**
     * 특정 타입의 알림 채널 조회
     */
    @Override
    public NotificationChannelInfo getChannelByType(Long userId, ChannelType channelType) {
        return channelRepository.findByUser_UserIdAndChannelType(userId, channelType)
                .map(this::toInfo)
                .orElse(null);
    }

    /**
     * 알림 채널 등록
     */
    @Override
    @Transactional
    public void registerChannel(Long userId, NotificationChannelCommand command) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보가 없습니다."));

        // 동일한 채널 타입이 이미 존재하는지 확인
        boolean exists = channelRepository.findByUser_UserIdAndChannelType(userId, command.channelType())
                .isPresent();

        if (exists) {
            throw new IllegalStateException("이미 등록된 채널 타입입니다.");
        }

        NotificationChannel channel = NotificationChannel.builder()
                .user(user)
                .channelType(command.channelType())
                .enabled(command.enabled() != null ? command.enabled() : true)
                .channelConfig(command.channelConfig())
                .build();

        channelRepository.save(channel);
    }

    /**
     * 알림 채널 설정 수정
     */
    @Override
    @Transactional
    public void updateChannel(Long userId, Long channelId, NotificationChannelCommand command) {
        NotificationChannel channel = channelRepository.findByChannelIdAndUser_UserId(channelId, userId)
                .orElseThrow(() -> new IllegalArgumentException("알림 채널 정보가 없거나 권한이 없습니다."));

        // 설정 정보 업데이트
        channel.updateConfig(command.channelConfig());

        // 활성화 상태 업데이트
        if (command.enabled() != null) {
            if (command.enabled()) {
                channel.enable();
            } else {
                channel.disable();
            }
        }
    }

    /**
     * 알림 채널 삭제
     */
    @Override
    @Transactional
    public void deleteChannel(Long userId, Long channelId) {
        NotificationChannel channel = channelRepository.findByChannelIdAndUser_UserId(channelId, userId)
                .orElseThrow(() -> new IllegalArgumentException("알림 채널 정보가 없거나 권한이 없습니다."));

        channelRepository.delete(channel);
    }

    /**
     * 알림 채널 활성화
     */
    @Override
    @Transactional
    public void enableChannel(Long userId, Long channelId) {
        NotificationChannel channel = channelRepository.findByChannelIdAndUser_UserId(channelId, userId)
                .orElseThrow(() -> new IllegalArgumentException("알림 채널 정보가 없거나 권한이 없습니다."));

        channel.enable();
    }

    /**
     * 알림 채널 비활성화
     */
    @Override
    @Transactional
    public void disableChannel(Long userId, Long channelId) {
        NotificationChannel channel = channelRepository.findByChannelIdAndUser_UserId(channelId, userId)
                .orElseThrow(() -> new IllegalArgumentException("알림 채널 정보가 없거나 권한이 없습니다."));

        channel.disable();
    }

    /**
     * 엔티티를 DTO로 변환
     */
    private NotificationChannelInfo toInfo(NotificationChannel channel) {
        return NotificationChannelInfo.builder()
                .channelId(channel.getChannelId())
                .channelType(channel.getChannelType())
                .enabled(channel.getEnabled())
                .channelConfig(channel.getChannelConfig())
                .build();
    }
}
