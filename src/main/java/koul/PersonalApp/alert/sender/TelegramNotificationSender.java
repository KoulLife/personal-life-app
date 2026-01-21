package koul.PersonalApp.alert.sender;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Telegram 알림 발송 서비스
 * Telegram Bot API를 사용하여 메시지 전송
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramNotificationSender implements NotificationSender {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Telegram Bot API로 메시지 전송
     */
    @Override
    public void send(String channelConfig, String message) {
        try {
            // JSON에서 botToken과 chatId 추출
            JsonNode config = objectMapper.readTree(channelConfig);
            String botToken = config.get("botToken").asText();
            String chatId = config.get("chatId").asText();

            // Telegram API URL
            String url = String.format("https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s",
                    botToken, chatId, java.net.URLEncoder.encode(message, "UTF-8"));

            restTemplate.getForEntity(url, String.class);

            log.info("Telegram 알림 전송 완료: {}", message);
        } catch (Exception e) {
            log.error("Telegram 알림 전송 실패: {}", e.getMessage(), e);
        }
    }
}
