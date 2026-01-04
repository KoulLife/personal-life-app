package koul.PersonalApp.project.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import koul.PersonalApp.project.Entity.Project;
import koul.PersonalApp.project.dto.ProjectCreateCommand;
import koul.PersonalApp.project.repository.ProjectRepository;

@Service
public class ProjectServiceImpl implements ProjectService {

	private ProjectRepository projectRepository;

	/**
	 * 프로젝트 관계 연결
	 */
	@Override
	@Transactional
	public void connectProjects(Long currentProjectId, Long nextProjectId) {
		Project currentProject = projectRepository.findById(currentProjectId)
			.orElseThrow(() -> new IllegalArgumentException("해당 ID의 프로젝트가 존재하지 않습니다: " + currentProjectId));

		Project nextProject = projectRepository.findById(nextProjectId)
			.orElseThrow(() -> new IllegalArgumentException("대상 ID의 프로젝트가 존재하지 않습니다: " + currentProjectId));
		// 다음 프로젝트 연결
		currentProject.connectToNextProject(nextProject);
		// 이전 프로젝트 연결
		nextProject.connectToPrevProject(currentProject);
	}

	/**
	 * 루트 프로젝트인지 체크
	 */
	@Override
	public boolean isRootProject(Long projectId) {
		Project project = projectRepository.findById(projectId)
			.orElseThrow(() -> new IllegalArgumentException("해당 ID의 프로젝트가 존재하지 않습니다: " + projectId));

		return project.isRootProject();
	}

	/**
	 * 완료된 프로젝트인지 확인
	 */
	@Override
	public boolean isProjectCompleted(Long projectId) {
		Project project = projectRepository.findById(projectId)
			.orElseThrow(() -> new IllegalArgumentException("해당 ID의 프로젝트가 존재하지 않습니다: " + projectId));

		return project.isCompleteStatus();
	}

	/**
	 * 프로젝트 생성
	 */
	@Override
	@Transactional
	public Long createProject(ProjectCreateCommand projectDto) {
		Project project = projectDto.toEntity();
		Project savedProject = projectRepository.save(project);

		return savedProject.getProjectId();
	}

	/**
	 * 프로젝트 삭제
	 */
	@Override
	@Transactional
	public void deleteProject(Long projectId) {
		try {
			projectRepository.deleteById(projectId);
		} catch (Exception e) {
			throw new RuntimeException("해당 ID가 존재하지 않습니다.");
		}

	}

	/**
	 * 프로젝트 업데이트
	 */
	@Override
	@Transactional
	public void updateProject(Long projectId, String content) {
		Project project = projectRepository.findById(projectId)
			.orElseThrow(() -> new IllegalArgumentException("해당 ID의 프로젝트가 존재하지 않습니다: " + projectId));

		project.changeContent(content);
	}

	/**
	 * 프로젝트 완료 처리
	 */
	@Override
	@Transactional
	public void completeProject(Long projectId) {
		Project project = projectRepository.findById(projectId)
			.orElseThrow(() -> new IllegalArgumentException("해당 ID의 프로젝트가 존재하지 않습니다: " + projectId));

		// 프로젝트 상태 완성으로 수정
		project.changeCompleteStatus(true);

		projectRepository.save(project);
	}

	/**
	 * 프로젝트 완료 처리 철회
	 */
	@Override
	public void undoCompleteProject(Long projectId) {
		Project project = projectRepository.findById(projectId)
			.orElseThrow(() -> new IllegalArgumentException("해당 ID의 프로젝트가 존재하지 않습니다: " + projectId));

		// 프로젝트 상태 미완성으로 수정
		project.changeCompleteStatus(false);

		projectRepository.save(project);
	}
}
