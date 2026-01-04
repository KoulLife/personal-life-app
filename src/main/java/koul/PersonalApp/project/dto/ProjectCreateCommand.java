package koul.PersonalApp.project.dto;

import java.util.Objects;

import jakarta.validation.constraints.NotBlank;
import koul.PersonalApp.project.Entity.Project;
import koul.PersonalApp.project.Entity.ProjectGroup;

public final class ProjectCreateCommand {
	private final @NotBlank(message = "프로젝트 내용은 필수 입력입니다.") String content;
	private final Boolean completeStatus;
	private final ProjectGroup projectGroup;

	public ProjectCreateCommand(
		// 필수 체크
		@NotBlank(message = "프로젝트 내용은 필수 입력입니다.")
		String content,

		Boolean completeStatus,
		ProjectGroup projectGroup
	) {
		this.content = content;
		this.completeStatus = completeStatus;
		this.projectGroup = projectGroup;
	}

	// DTO -> Entity 변환 메서드
	public Project toEntity() {
		return Project.builder()
			.content(this.content)
			// 값이 없으면(null) false, 있으면 그 값 사용
			.completeStatus(this.completeStatus != null && this.completeStatus)
			.prevProject(null) // 생성 시점엔 부모 연결은 Service에서 따로 함
			.nextProjects(null) // 생성 시점엔 자식 없음
			.projectGroup(projectGroup)
			.build();
	}

	public @NotBlank(message = "프로젝트 내용은 필수 입력입니다.") String content() {
		return content;
	}

	public Boolean completeStatus() {
		return completeStatus;
	}

	public ProjectGroup projectGroup() {
		return projectGroup;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj == null || obj.getClass() != this.getClass())
			return false;
		var that = (ProjectCreateCommand)obj;
		return Objects.equals(this.content, that.content) &&
			Objects.equals(this.completeStatus, that.completeStatus) &&
			Objects.equals(this.projectGroup, that.projectGroup);
	}

	@Override
	public int hashCode() {
		return Objects.hash(content, completeStatus, projectGroup);
	}

	@Override
	public String toString() {
		return "ProjectCreateCommand[" +
			"content=" + content + ", " +
			"completeStatus=" + completeStatus + ", " +
			"projectGroup=" + projectGroup + ']';
	}

}