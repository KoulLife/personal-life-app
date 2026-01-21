package koul.PersonalApp.alert.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import koul.PersonalApp.alert.dto.NotificationChannelCommand;
import koul.PersonalApp.alert.dto.NotificationChannelInfo;
import koul.PersonalApp.alert.entity.ChannelType;
import koul.PersonalApp.alert.service.NotificationChannelService;
import koul.PersonalApp.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;

/**
 * 알림 채널 관리 컨트롤러
 * 사용자별 알림 채널 설정 및 관리를 위한 REST API
 */
@RestController
@RequestMapping("/alert/channels")
@RequiredArgsConstructor
public class NotificationChannelController {

    private final NotificationChannelService channelService;

    /**
     * 사용자의 모든 알림 채널 조회
     */
    @GetMapping
    public ResponseEntity<List<NotificationChannelInfo>> getAllChannels(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<NotificationChannelInfo> channels = channelService.getAllChannels(userDetails.getUserId());
        return ResponseEntity.ok(channels);
    }

    /**
     * 사용자의 활성화된 알림 채널만 조회
     */
    @GetMapping("/enabled")
    public ResponseEntity<List<NotificationChannelInfo>> getEnabledChannels(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<NotificationChannelInfo> channels = channelService.getEnabledChannels(userDetails.getUserId());
        return ResponseEntity.ok(channels);
    }

    /**
     * 특정 타입의 알림 채널 조회
     */
    @GetMapping("/type/{channelType}")
    public ResponseEntity<NotificationChannelInfo> getChannelByType(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("channelType") ChannelType channelType) {
        NotificationChannelInfo channel = channelService.getChannelByType(userDetails.getUserId(), channelType);
        return ResponseEntity.ok(channel);
    }

    /**
     * 알림 채널 등록
     */
    @PostMapping
    public ResponseEntity<String> registerChannel(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid NotificationChannelCommand command) {
        channelService.registerChannel(userDetails.getUserId(), command);
        return ResponseEntity.ok("알림 채널이 성공적으로 등록되었습니다.");
    }

    /**
     * 알림 채널 설정 수정
     */
    @PatchMapping("/{channelId}")
    public ResponseEntity<String> updateChannel(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("channelId") Long channelId,
            @RequestBody @Valid NotificationChannelCommand command) {
        channelService.updateChannel(userDetails.getUserId(), channelId, command);
        return ResponseEntity.ok("알림 채널이 성공적으로 수정되었습니다.");
    }

    /**
     * 알림 채널 삭제
     */
    @DeleteMapping("/{channelId}")
    public ResponseEntity<String> deleteChannel(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("channelId") Long channelId) {
        channelService.deleteChannel(userDetails.getUserId(), channelId);
        return ResponseEntity.ok("알림 채널이 성공적으로 삭제되었습니다.");
    }

    /**
     * 알림 채널 활성화
     */
    @PatchMapping("/{channelId}/enable")
    public ResponseEntity<String> enableChannel(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("channelId") Long channelId) {
        channelService.enableChannel(userDetails.getUserId(), channelId);
        return ResponseEntity.ok("알림 채널이 활성화되었습니다.");
    }

    /**
     * 알림 채널 비활성화
     */
    @PatchMapping("/{channelId}/disable")
    public ResponseEntity<String> disableChannel(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("channelId") Long channelId) {
        channelService.disableChannel(userDetails.getUserId(), channelId);
        return ResponseEntity.ok("알림 채널이 비활성화되었습니다.");
    }
}
