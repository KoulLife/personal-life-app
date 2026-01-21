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
import koul.PersonalApp.alert.dto.AlertRuleCommand;
import koul.PersonalApp.alert.dto.AlertRuleInfo;
import koul.PersonalApp.alert.service.AlertRuleService;
import koul.PersonalApp.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;

/**
 * 알림 규칙 관리 컨트롤러
 * 서비스별 알림 채널 매핑 설정 API
 */
@RestController
@RequestMapping("/alert/rules")
@RequiredArgsConstructor
public class AlertRuleController {

    private final AlertRuleService ruleService;

    /**
     * 사용자의 모든 알림 규칙 조회
     */
    @GetMapping
    public ResponseEntity<List<AlertRuleInfo>> getAllRules(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<AlertRuleInfo> rules = ruleService.getAllRules(userDetails.getUserId());
        return ResponseEntity.ok(rules);
    }

    /**
     * 알림 규칙 생성 (서비스-채널 매핑)
     */
    @PostMapping
    public ResponseEntity<String> createRule(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid AlertRuleCommand command) {
        ruleService.createRule(userDetails.getUserId(), command);
        return ResponseEntity.ok("알림 규칙이 생성되었습니다.");
    }

    /**
     * 알림 규칙 삭제
     */
    @DeleteMapping("/{ruleId}")
    public ResponseEntity<String> deleteRule(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("ruleId") Long ruleId) {
        ruleService.deleteRule(userDetails.getUserId(), ruleId);
        return ResponseEntity.ok("알림 규칙이 삭제되었습니다.");
    }

    /**
     * 알림 규칙 활성화
     */
    @PatchMapping("/{ruleId}/enable")
    public ResponseEntity<String> enableRule(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("ruleId") Long ruleId) {
        ruleService.enableRule(userDetails.getUserId(), ruleId);
        return ResponseEntity.ok("알림 규칙이 활성화되었습니다.");
    }

    /**
     * 알림 규칙 비활성화
     */
    @PatchMapping("/{ruleId}/disable")
    public ResponseEntity<String> disableRule(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("ruleId") Long ruleId) {
        ruleService.disableRule(userDetails.getUserId(), ruleId);
        return ResponseEntity.ok("알림 규칙이 비활성화되었습니다.");
    }
}
