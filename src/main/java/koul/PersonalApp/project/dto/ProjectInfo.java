package koul.PersonalApp.project.dto;

import koul.PersonalApp.project.Entity.Project;
import java.util.List;

public record ProjectInfo(
	Long projectId,
	String content,
	boolean completeStatus,
	boolean isRoot,
	Long prevProjectId,       // 부모 ID만 전달
	List<Long> nextProjectIds // 자식들의 ID 목록만 전달
) {
	// Entity -> DTO 변환 메서드 (Factory Method)
	public static ProjectInfo from(Project project) {
		// 부모 ID 추출 (null 안전 처리)
		Long prevId = (project.getPrevProject() != null) ?
			project.getPrevProject().getProjectId() : null;

		// 자식 ID 리스트 추출 (Stream 사용)
		List<Long> nextIds = project.getNextProjects().stream()
			.map(Project::getProjectId)
			.toList();

		return new ProjectInfo(
			project.getProjectId(),
			project.getContent(),
			project.isCompleteStatus(),
			project.isRootProject(),
			prevId,
			nextIds
		);
	}
}