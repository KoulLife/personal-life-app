package koul.PersonalApp.alert.entity;

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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import koul.PersonalApp.global.entity.BaseTimeEntity;
import koul.PersonalApp.user.entity.ServiceType;
import koul.PersonalApp.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 알림 규칙 엔티티
 * 특정 서비스(ServiceType)의 이벤트를 어떤 채널(NotificationChannel)로 받을지 매핑 정보 저장
 */
@Entity
@Getter
@NoArgsConstructor
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = { "user_id", "serviceType", "channel_id" })
})
public class AlertRule extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ruleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceType serviceType; // 알림을 보내는 서비스 (예: PROJECT_MANAGER)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    private NotificationChannel channel; // 알림을 받을 채널

    @Column(nullable = false)
    private Boolean enabled = true; // 규칙 활성화 여부

    @Builder
    public AlertRule(User user, ServiceType serviceType, NotificationChannel channel, Boolean enabled) {
        this.user = user;
        this.serviceType = serviceType;
        this.channel = channel;
        this.enabled = enabled != null ? enabled : true;
    }

    public void enable() {
        this.enabled = true;
    }

    public void disable() {
        this.enabled = false;
    }
}
