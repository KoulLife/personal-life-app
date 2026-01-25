package koul.PersonalApp.alert.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import koul.PersonalApp.alert.dto.EventPayload;
import koul.PersonalApp.alert.entity.AlertRule;
import koul.PersonalApp.alert.entity.ChannelType;
import koul.PersonalApp.alert.entity.NotificationChannel;
import koul.PersonalApp.alert.repository.AlertRuleRepository;
import koul.PersonalApp.alert.repository.NotificationChannelRepository;
import koul.PersonalApp.alert.sender.EmailNotificationSender;
import koul.PersonalApp.alert.sender.SlackNotificationSender;
import koul.PersonalApp.alert.sender.TelegramNotificationSender;
import koul.PersonalApp.user.entity.ServiceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 알림 처리 서비스 구현체
 * 이벤트를 받아 사용자의 활성화된 채널로 알림을 비동기로 발송
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlertServiceImpl implements AlertService {

    private final NotificationChannelRepository channelRepository;
    private final AlertRuleRepository ruleRepository;
    private final SlackNotificationSender slackSender;
    private final TelegramNotificationSender telegramSender;
    private final EmailNotificationSender emailSender;

    /**
     * 이벤트를 비동기로 처리하여 알림 발송
     * 
     * @Async 덕분에 요청을 보낸 서비스는 즉시 응답을 받음
     */
    @Async
    @Override
    public void processEventAsync(EventPayload event) {
        log.info("이벤트 처리 시작 - 서비스: {}, 타입: {}, 사용자: {}",
                event.serviceName(), event.eventType(), event.userId());

        // 서비스 정보를 포함하여 알림 전송 (규칙 적용)
        sendNotification(event.userId(), event.serviceName(), event.message());

        log.info("이벤트 처리 완료 - 사용자: {}", event.userId());
    }

    /**
     * 특정 사용자에게 메시지 전송 (모든 채널)
     * 기존 호환성을 위해 유지
     */
    @Override
    public void sendNotification(Long userId, String message) {
        // 사용자의 활성화된 모든 채널 조회
        List<NotificationChannel> channels = channelRepository.findByUserIdAndEnabledTrue(userId);
        sendToChannels(channels, message);
    }

    /**
     * 특정 사용자에게 서비스별 규칙을 적용하여 알림 전송
     */
    @Override
    public void sendNotification(Long userId, ServiceType serviceType, String message) {
        // 1. 해당 서비스에 대한 활성화된 규칙 조회
        List<AlertRule> rules = ruleRepository.findActiveRulesByService(userId, serviceType);

        if (!rules.isEmpty()) {
            // 규칙이 있으면 해당 채널들로만 발송
            List<NotificationChannel> channels = rules.stream()
                    .map(AlertRule::getChannel)
                    .collect(Collectors.toList());
            log.info("사용자 {}의 서비스 {}에 대한 알림 규칙 {}개 적용", userId, serviceType, channels.size());
            sendToChannels(channels, message);
        } else {
            // 규칙이 없으면?
            // 정책 결정: 규칙이 없으면 기본적으로 모든 채널로 보낼지, 아니면 안 보낼지?
            // 여기서는 "규칙이 없으면 모든 채널로 발송"하는 것이 안전할 수 있음 (기존 동작 유지)
            // 하지만 사용자가 명시적으로 제어하고 싶어하는 요구사항이므로,
            // "규칙이 하나라도 존재하면 규칙 따름", "규칙이 아예 없으면 기본 채널(모든 채널)" 로직이 적절해보임.
            // 하지만 "Project는 Slack만" 이라고 설정했는데, Financial은 설정 안해서 다 가는게 맞나?
            // 사용자 요구사항: "서비스와 알림수단을 등록" 하기를 원함.
            // 즉, 등록 안하면 안 가는게 맞을 수도 있지만, 초기 설정의 편의성을 위해
            // "규칙이 하나라도 정의되어 있으면 그 규칙만 따르고, 아예 없으면 Fallback으로 모든 채널" 전략 사용.
            // 혹은 사용자가 아예 설정을 안했으면 다 보내고, 설정을 시작하면 설정된 것만 보내기.

            // 일단 간단하게: **규칙이 있으면 규칙대로, 없으면 모든 채널** 로 구현.
            boolean hasAnyRule = !ruleRepository.findByUser_UserId(userId).isEmpty();
            if (hasAnyRule) {
                log.info("사용자 {}는 알림 규칙을 설정했으나 서비스 {}에 대한 규칙이 없습니다. 알림을 보내지 않습니다.", userId, serviceType);
            } else {
                log.info("사용자 {}의 알림 규칙이 없습니다. 모든 채널로 발송합니다.", userId);
                sendNotification(userId, message);
            }
        }
    }

    private void sendToChannels(List<NotificationChannel> channels, String message) {
        if (channels.isEmpty()) {
            log.warn("발송 가능한 알림 채널이 없습니다.");
            return;
        }

        for (NotificationChannel channel : channels) {
            try {
                sendToChannel(channel, message);
            } catch (Exception e) {
                log.error("채널 {} 알림 발송 실패: {}", channel.getChannelType(), e.getMessage(), e);
            }
        }
    }

    /**
     * 채널 타입에 따라 적절한 Sender로 메시지 전송
     */
    private void sendToChannel(NotificationChannel channel, String message) {
        ChannelType type = channel.getChannelType();
        String config = channel.getChannelConfig();

        switch (type) {
            case SLACK:
                slackSender.send(config, message);
                break;
            case TELEGRAM:
                telegramSender.send(config, message);
                break;
            case EMAIL:
                emailSender.send(config, message);
                break;
            default:
                log.warn("지원하지 않는 채널 타입: {}", type);
        }
    }
}
