package koul.PersonalApp.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ServiceType {
    PROJECT_MANAGER("프로젝트 관리");

    private final String description;
}
