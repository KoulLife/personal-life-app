package koul.PersonalApp.alert.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import koul.PersonalApp.alert.dto.AlertRuleCommand;
import koul.PersonalApp.alert.dto.AlertRuleInfo;
import koul.PersonalApp.alert.entity.AlertRule;
import koul.PersonalApp.alert.entity.NotificationChannel;
import koul.PersonalApp.alert.repository.AlertRuleRepository;
import koul.PersonalApp.alert.repository.NotificationChannelRepository;
import koul.PersonalApp.user.entity.User;
import koul.PersonalApp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlertRuleServiceImpl implements AlertRuleService {

    private final AlertRuleRepository ruleRepository;
    private final NotificationChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<AlertRuleInfo> getAllRules(Long userId) {
        return ruleRepository.findByUser_UserId(userId).stream()
                .map(this::toInfo)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void createRule(Long userId, AlertRuleCommand command) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보가 없습니다."));

        // 채널 조회 및 소유권 확인
        NotificationChannel channel = channelRepository.findByChannelIdAndUser_UserId(command.channelId(), userId)
                .orElseThrow(() -> new IllegalArgumentException("채널을 찾을 수 없거나 권한이 없습니다."));

        // 중복 규칙 확인
        if (ruleRepository.existsByUser_UserIdAndServiceTypeAndChannel_ChannelId(userId, command.serviceType(),
                command.channelId())) {
            throw new IllegalStateException("이미 존재하는 규칙입니다.");
        }

        AlertRule rule = AlertRule.builder()
                .user(user)
                .serviceType(command.serviceType())
                .channel(channel)
                .enabled(command.enabled() != null ? command.enabled() : true)
                .build();

        ruleRepository.save(rule);
    }

    @Override
    @Transactional
    public void deleteRule(Long userId, Long ruleId) {
        AlertRule rule = ruleRepository.findByRuleIdAndUser_UserId(ruleId, userId)
                .orElseThrow(() -> new IllegalArgumentException("규칙을 찾을 수 없거나 권한이 없습니다."));

        ruleRepository.delete(rule);
    }

    @Override
    @Transactional
    public void enableRule(Long userId, Long ruleId) {
        AlertRule rule = ruleRepository.findByRuleIdAndUser_UserId(ruleId, userId)
                .orElseThrow(() -> new IllegalArgumentException("규칙을 찾을 수 없거나 권한이 없습니다."));
        rule.enable();
    }

    @Override
    @Transactional
    public void disableRule(Long userId, Long ruleId) {
        AlertRule rule = ruleRepository.findByRuleIdAndUser_UserId(ruleId, userId)
                .orElseThrow(() -> new IllegalArgumentException("규칙을 찾을 수 없거나 권한이 없습니다."));
        rule.disable();
    }

    private AlertRuleInfo toInfo(AlertRule rule) {
        String channelName = getChannelIdentifier(rule.getChannel());

        return AlertRuleInfo.builder()
                .ruleId(rule.getRuleId())
                .serviceType(rule.getServiceType())
                .channelId(rule.getChannel().getChannelId())
                .channelType(rule.getChannel().getChannelType())
                .channelName(channelName)
                .enabled(rule.getEnabled())
                .build();
    }

    // 채널 설정 JSON에서 식별 가능한 정보 추출 (이메일 주소, 웹훅 등)
    private String getChannelIdentifier(NotificationChannel channel) {
        try {
            JsonNode config = objectMapper.readTree(channel.getChannelConfig());
            switch (channel.getChannelType()) {
                case EMAIL:
                    return config.has("emailAddress") ? config.get("emailAddress").asText() : "Email";
                case SLACK:
                    return "Slack Webhook";
                case TELEGRAM:
                    return "Telegram Bot";
                default:
                    return channel.getChannelType().name();
            }
        } catch (JsonProcessingException e) {
            return channel.getChannelType().name();
        }
    }
}
