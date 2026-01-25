package koul.PersonalApp.alert.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import koul.PersonalApp.alert.entity.AlertRule;
import koul.PersonalApp.user.entity.ServiceType;

public interface AlertRuleRepository extends JpaRepository<AlertRule, Long> {

    /**
     * 사용자의 모든 알림 규칙 조회
     */
    List<AlertRule> findByUser_UserId(Long userId);

    /**
     * 사용자의 특정 서비스에 대한 활성화된 알림 규칙 조회 (알림 발송용)
     * - 규칙이 활성화되어 있고(enabled=true)
     * - 연결된 채널도 활성화되어 있어야 함(channel.enabled=true)
     */
    @Query("SELECT ar FROM AlertRule ar " +
            "JOIN FETCH ar.channel c " +
            "WHERE ar.user.userId = :userId " +
            "AND ar.serviceType = :serviceType " +
            "AND ar.enabled = true " +
            "AND c.enabled = true")
    List<AlertRule> findActiveRulesByService(@Param("userId") Long userId,
            @Param("serviceType") ServiceType serviceType);

    /**
     * 중복 규칙 확인용
     */
    boolean existsByUser_UserIdAndServiceTypeAndChannel_ChannelId(Long userId, ServiceType serviceType, Long channelId);

    /**
     * ID와 사용자 ID로 조회 (권한 확인용)
     */
    Optional<AlertRule> findByRuleIdAndUser_UserId(Long ruleId, Long userId);
}
