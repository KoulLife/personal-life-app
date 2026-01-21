package koul.PersonalApp.alert.sender;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Email 알림 발송 서비스
 * Spring Mail을 사용하여 Gmail SMTP로 이메일 전송
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailNotificationSender implements NotificationSender {

    private final JavaMailSender mailSender;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 이메일로 메시지 전송
     */
    @Override
    public void send(String channelConfig, String message) {
        try {
            // JSON에서 emailAddress 추출
            JsonNode config = objectMapper.readTree(channelConfig);
            String emailAddress = config.get("emailAddress").asText();

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(emailAddress);
            mailMessage.setSubject("알림: Personal Life App");
            mailMessage.setText(message);

            mailSender.send(mailMessage);

            log.info("Email 알림 전송 완료: {}", emailAddress);
        } catch (Exception e) {
            log.error("Email 알림 전송 실패: {}", e.getMessage(), e);
        }
    }
}
