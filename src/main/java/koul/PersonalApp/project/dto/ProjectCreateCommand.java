package koul.PersonalApp.project.dto;

import jakarta.validation.constraints.NotBlank;
import koul.PersonalApp.project.Entity.Project;

public record ProjectCreateCommand(
	// 필수 체크
	@NotBlank(message = "프로젝트 내용은 필수 입력입니다.")
	String content,

	Boolean completeStatus
) {
	// DTO -> Entity 변환 메서드
	public Project toEntity() {
		return Project.builder()
			.content(this.content)
			// 값이 없으면(null) false, 있으면 그 값 사용
			.completeStatus(this.completeStatus != null && this.completeStatus)
			.prevProject(null) // 생성 시점엔 부모 연결은 Service에서 따로 함
			.nextProjects(null) // 생성 시점엔 자식 없음
			.build();
	}
}