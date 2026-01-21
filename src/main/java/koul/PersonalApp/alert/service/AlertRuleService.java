package koul.PersonalApp.alert.service;

import java.util.List;

import koul.PersonalApp.alert.dto.AlertRuleCommand;
import koul.PersonalApp.alert.dto.AlertRuleInfo;

public interface AlertRuleService {

    /**
     * 사용자의 모든 알림 규칙 조회
     */
    List<AlertRuleInfo> getAllRules(Long userId);

    /**
     * 알림 규칙 생성 (서비스-채널 매핑)
     */
    void createRule(Long userId, AlertRuleCommand command);

    /**
     * 알림 규칙 삭제
     */
    void deleteRule(Long userId, Long ruleId);

    /**
     * 알림 규칙 활성화
     */
    void enableRule(Long userId, Long ruleId);

    /**
     * 알림 규칙 비활성화
     */
    void disableRule(Long userId, Long ruleId);
}
