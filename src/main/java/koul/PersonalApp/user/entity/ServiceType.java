package koul.PersonalApp.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ServiceType {
    PROJECT_MANAGER("프로젝트 관리"),
    REALTIME_WORKFLOW("실시간 업무 관리"),
    ALERT_MANAGER("알람 관리"),
    FINANCIAL_MANAGER("재정 관리");

    private final String description;
}
