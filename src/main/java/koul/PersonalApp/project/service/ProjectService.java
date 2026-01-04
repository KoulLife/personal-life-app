package koul.PersonalApp.project.service;

import koul.PersonalApp.project.dto.ProjectCreateCommand;

public interface ProjectService {

	// 두 프로젝트를 연결
	void connectProjects(Long currentProjectId, Long nextProjectId);

	// 루트 프로젝트인지 확인
	boolean isRootProject(Long projectId);

	// 프로젝트 상태 확인 완료/미완료
	boolean isProjectCompleted(Long projectId);

	// 프로젝트 생성
	Long createProject(ProjectCreateCommand projectDto);

	// 프로젝트 삭제
	void deleteProject(Long projectId);

	// 프로젝트 내용 수정
	void updateProject(Long projectId, String content);

	// 프로젝트 완료
	void completeProject(Long projectId);

	// 프로젝트 완료 철회
	void undoCompleteProject(Long projectId);
}
