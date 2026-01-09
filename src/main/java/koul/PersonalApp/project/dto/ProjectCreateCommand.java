package koul.PersonalApp.project.dto;

import java.util.Objects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public final class ProjectCreateCommand {
	private final @NotBlank(message = "프로젝트 내용은 필수 입력입니다.") String content;
	private final Boolean completeStatus;
	private final Long projectGroupId;
	private final Long prevProjectId;
	private final @NotNull(message = "유저 ID는 필수입니다.") Long userId;

	@Builder
	public ProjectCreateCommand(
			String content,
			Boolean completeStatus,
			Long projectGroupId,
			Long prevProjectId,
			Long userId) {
		this.content = content;
		this.completeStatus = completeStatus;
		this.projectGroupId = projectGroupId;
		this.prevProjectId = prevProjectId;
		this.userId = userId;
	}

	public String content() {
		return content;
	}

	public Boolean completeStatus() {
		return completeStatus;
	}

	public Long projectGroupId() {
		return projectGroupId;
	}

	public Long prevProjectId() {
		return prevProjectId;
	}

	public Long userId() {
		return userId;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj == null || obj.getClass() != this.getClass())
			return false;
		var that = (ProjectCreateCommand) obj;
		return Objects.equals(this.content, that.content) &&
				Objects.equals(this.completeStatus, that.completeStatus) &&
				Objects.equals(this.projectGroupId, that.projectGroupId) &&
				Objects.equals(this.prevProjectId, that.prevProjectId) &&
				Objects.equals(this.userId, that.userId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(content, completeStatus, projectGroupId, prevProjectId, userId);
	}

	@Override
	public String toString() {
		return "ProjectCreateCommand[" +
				"content=" + content + ", " +
				"completeStatus=" + completeStatus + ", " +
				"projectGroupId=" + projectGroupId + ", " +
				"prevProjectId=" + prevProjectId + ", " +
				"userId=" + userId + ']';
	}

}