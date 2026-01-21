package koul.PersonalApp.alert.entity;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import koul.PersonalApp.global.entity.BaseTimeEntity;
import koul.PersonalApp.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자별 알림 채널 설정 엔티티
 * 각 사용자는 여러 알림 채널을 등록할 수 있으며, 채널별로 활성화/비활성화 가능
 */
@Entity
@Getter
@NoArgsConstructor
public class NotificationChannel extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long channelId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 어떤 사용자의 설정인지

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChannelType channelType; // 채널 타입 (SLACK, TELEGRAM, EMAIL)

    @Column(nullable = false)
    private Boolean enabled = true; // 활성화 여부

    /**
     * 채널별로 필요한 설정 정보를 JSON 형태로 저장
     * - SLACK: {"webhookUrl": "https://..."}
     * - TELEGRAM: {"botToken": "...", "chatId": "..."}
     * - EMAIL: {"emailAddress": "user@example.com"}
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String channelConfig;

    @Builder
    public NotificationChannel(User user, ChannelType channelType, Boolean enabled, String channelConfig) {
        this.user = user;
        this.channelType = channelType;
        this.enabled = enabled != null ? enabled : true;
        this.channelConfig = channelConfig;
    }

    /**
     * 채널 활성화
     */
    public void enable() {
        this.enabled = true;
    }

    /**
     * 채널 비활성화
     */
    public void disable() {
        this.enabled = false;
    }

    /**
     * 채널 설정 정보 업데이트
     */
    public void updateConfig(String channelConfig) {
        this.channelConfig = channelConfig;
    }
}
