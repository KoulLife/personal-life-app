package koul.PersonalApp.alert.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import koul.PersonalApp.alert.entity.ChannelType;
import koul.PersonalApp.alert.entity.NotificationChannel;

public interface NotificationChannelRepository extends JpaRepository<NotificationChannel, Long> {

    /**
     * 특정 사용자의 모든 알림 채널 조회
     */
    List<NotificationChannel> findByUser_UserId(Long userId);

    /**
     * 특정 사용자의 활성화된 알림 채널만 조회
     */
    @Query("SELECT nc FROM NotificationChannel nc WHERE nc.user.userId = :userId AND nc.enabled = true")
    List<NotificationChannel> findByUserIdAndEnabledTrue(@Param("userId") Long userId);

    /**
     * 특정 사용자의 특정 타입 채널 조회
     */
    Optional<NotificationChannel> findByUser_UserIdAndChannelType(Long userId, ChannelType channelType);

    /**
     * 특정 사용자의 특정 채널 ID로 조회 (소유권 확인용)
     */
    Optional<NotificationChannel> findByChannelIdAndUser_UserId(Long channelId, Long userId);
}
