package koul.PersonalApp.alert.sender;

/**
 * 알림 발송 인터페이스
 * 각 채널별 구현체(Slack, Telegram, Email)가 이 인터페이스를 구현
 */
public interface NotificationSender {

    /**
     * 알림 전송
     * 
     * @param channelConfig 채널 설정 정보 (JSON 문자열)
     * @param message       전송할 메시지
     */
    void send(String channelConfig, String message);
}
