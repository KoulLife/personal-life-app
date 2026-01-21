package koul.PersonalApp.alert.sender;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Slack 알림 발송 서비스
 * Webhook URL을 사용하여 Slack으로 메시지 전송
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SlackNotificationSender implements NotificationSender {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Slack Webhook으로 메시지 전송
     */
    @Override
    public void send(String channelConfig, String message) {
        try {
            // JSON에서 webhookUrl 추출
            JsonNode config = objectMapper.readTree(channelConfig);
            String webhookUrl = config.get("webhookUrl").asText();

            // Slack 메시지 포맷
            String payload = String.format("{\"text\": \"%s\"}", escapeJson(message));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> request = new HttpEntity<>(payload, headers);
            restTemplate.postForEntity(webhookUrl, request, String.class);

            log.info("Slack 알림 전송 완료: {}", message);
        } catch (Exception e) {
            log.error("Slack 알림 전송 실패: {}", e.getMessage(), e);
        }
    }

    /**
     * JSON 문자열 이스케이프 처리
     */
    private String escapeJson(String text) {
        return text.replace("\"", "\\\"").replace("\n", "\\n");
    }
}
