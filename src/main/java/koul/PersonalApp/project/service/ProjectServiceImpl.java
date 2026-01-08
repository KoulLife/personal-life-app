package koul.PersonalApp.project.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import koul.PersonalApp.project.Entity.Project;
import koul.PersonalApp.project.Entity.ProjectGroup;
import koul.PersonalApp.project.dto.ProjectCreateCommand;
import koul.PersonalApp.project.repository.ProjectGroupRepository;
import koul.PersonalApp.project.repository.ProjectRepository;
import koul.PersonalApp.user.entity.User;
import koul.PersonalApp.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

	private final ProjectRepository projectRepository;
	private final ProjectGroupRepository projectGroupRepository;
	private final UserRepository userRepository;

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
	public Long createProject(ProjectCreateCommand command) {
		// 유저 조회 (존재 여부 확인)
		User user = userRepository.findById(command.userId())
				.orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다: " + command.userId()));

		ProjectGroup projectGroup;
		Project prevProject = null;

		// 1. 이전 프로젝트가 있는 경우 (그룹 상속)
		if (command.prevProjectId() != null) {
			prevProject = projectRepository.findById(command.prevProjectId())
					.orElseThrow(() -> new IllegalArgumentException("이전 프로젝트를 찾을 수 없습니다: " + command.prevProjectId()));

			// 부모 프로젝트 소유권 확인
			if (!prevProject.getUser().getUserId().equals(command.userId())) {
				throw new IllegalArgumentException("이전 프로젝트에 대한 접근 권한이 없습니다.");
			}

			// 그룹 상속 (부모 프로젝트의 그룹을 그대로 사용)
			projectGroup = prevProject.getProjectGroup();
		}
		// 2. 이전 프로젝트가 없는 경우 (그룹 ID 필수)
		else {
			if (command.projectGroupId() == null) {
				throw new IllegalArgumentException("프로젝트 그룹 ID는 필수입니다.");
			}
			// 그룹 조회 및 소유권 확인
			projectGroup = projectGroupRepository.findByProjectGroupIdAndUser_UserId(
					command.projectGroupId(),
					command.userId())
					.orElseThrow(() -> new IllegalArgumentException(
							"해당 그룹을 찾을 수 없거나 접근 권한이 없습니다. ID: " + command.projectGroupId()));
		}

		// 프로젝트 생성
		Project project = Project.builder()
				.content(command.content())
				.completeStatus(command.completeStatus() != null && command.completeStatus())
				.projectGroup(projectGroup)
				.prevProject(prevProject) // 이전 프로젝트 설정
				.build();

		// 유저 연관관계 설정
		project.assignUser(user);

		// 저장
		Project savedProject = projectRepository.save(project);

		// 그룹에도 프로젝트 추가
		projectGroup.addProject(savedProject);

		// 이전 프로젝트가 있다면, 이전 프로젝트의 '다음 프로젝트' 리스트에도 추가됨
		if (prevProject != null) {
			prevProject.connectToNextProject(savedProject);
		}

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
