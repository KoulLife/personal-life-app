package koul.PersonalApp.project.service;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import koul.PersonalApp.project.Entity.Project;
import koul.PersonalApp.project.Entity.ProjectGroup;
import koul.PersonalApp.project.repository.ProjectGroupRepository;
import koul.PersonalApp.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectGroupServiceImpl implements ProjectGroupService {

	private final ProjectGroupRepository projectGroupRepository;
	private final ProjectRepository projectRepository;

	/**
	 * 그룹에 프로젝트 추가
	 * TODO: 추후에 권한 체크 로직 추가 필요
	 */
	@Override
	@Transactional
	public void addProjectToGroup(Long projectGroupId, Long projectId) {
		// 반복되는 조회 로직은 private 메서드로 뺌 (하단 참조)
		ProjectGroup projectGroup = findProjectGroupById(projectGroupId);
		Project project = findProjectById(projectId);

		projectGroup.addProject(project);
	}

	/**
	 * 그룹에서 프로젝트 제거
	 */
	@Override
	@Transactional
	public void removeProjectFromGroup(Long projectGroupId, Long projectId) {
		ProjectGroup projectGroup = findProjectGroupById(projectGroupId);
		Project project = findProjectById(projectId);

		projectGroup.removeProject(project);
	}

	/**
	 * 그룹 내 모든 프로젝트 ID 목록 조회
	 */
	@Override
	public List<Long> findAllProjectIdsByGroup(Long projectGroupId) {
		ProjectGroup projectGroup = findProjectGroupById(projectGroupId);
		return projectGroup.getAllProjectIdsInfo();
	}

	/**
	 * 프로젝트 총 개수 조회
	 */
	@Override
	public long getTotalProjectCount(Long projectGroupId) {
		ProjectGroup projectGroup = findProjectGroupById(projectGroupId);
		return projectGroup.getTotalProjectCount();
	}

	/**
	 * 완료된 프로젝트 개수 조회
	 */
	@Override
	public long getCompletedProjectCount(Long projectGroupId) {
		ProjectGroup projectGroup = findProjectGroupById(projectGroupId);
		return (int) projectGroup.getCompletedProjectCount();
	}

	/**
	 * 프로젝트 그룹 생성
	 */
	@Override
	@Transactional
	public Long createGroup() {
		ProjectGroup projectGroup = new ProjectGroup();
		ProjectGroup savedProjectGroup = projectGroupRepository.save(projectGroup);

		return savedProjectGroup.getProjectGroupId();
	}

	@Override
	@Transactional
	public void deleteGroup(Long projectGroupId) {
		// 없는 걸 지우려고 하면 예외 터트리기
		if (!projectGroupRepository.existsById(projectGroupId)) {
			throw new IllegalArgumentException("삭제하려는 그룹이 존재하지 않습니다: " + projectGroupId);
		}

		projectGroupRepository.deleteById(projectGroupId);
	}

	/**
	 * 헬퍼 메서드 코드 중복 제거
	 */
	private ProjectGroup findProjectGroupById(Long id) {
		return projectGroupRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("해당 ID의 그룹이 존재하지 않습니다. ID: " + id));
	}

	private Project findProjectById(Long id) {
		return projectRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("해당 ID의 프로젝트가 존재하지 않습니다. ID: " + id));
	}
}
