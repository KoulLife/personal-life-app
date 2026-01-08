package koul.PersonalApp.project.dto;

import jakarta.validation.constraints.NotBlank;

public record ProjectRequest(
                @NotBlank(message = "프로젝트 내용은 필수 입력입니다.") String content,
                Long projectGroupId,
                Long prevProjectId) {
}
