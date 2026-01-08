package koul.PersonalApp.project.service;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import koul.PersonalApp.project.Entity.Project;
import koul.PersonalApp.project.Entity.ProjectGroup;
import koul.PersonalApp.project.dto.ProjectGroupCreateCommand;
import koul.PersonalApp.project.dto.ProjectGroupDetailInfo;
import koul.PersonalApp.project.dto.ProjectGroupSummaryInfo;
import koul.PersonalApp.project.repository.ProjectGroupRepository;
import koul.PersonalApp.project.repository.ProjectRepository;
import koul.PersonalApp.user.entity.User;
import koul.PersonalApp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectGroupServiceImpl implements ProjectGroupService {

	private final ProjectGroupRepository projectGroupRepository;
	private final ProjectRepository projectRepository;
	private final UserRepository userRepository;

	/**
	 * 그룹에 프로젝트 추가
	 */
	@Override
	@Transactional
	public void addProjectToGroup(Long userId, Long projectGroupId, Long projectId) {
		ProjectGroup projectGroup = findProjectGroupByIdAndValidateOwner(projectGroupId, userId);
		Project project = findProjectById(projectId);

		projectGroup.addProject(project);
	}

	/**
	 * 그룹에서 프로젝트 제거
	 */
	@Override
	@Transactional
	public void removeProjectFromGroup(Long userId, Long projectGroupId, Long projectId) {
		ProjectGroup projectGroup = findProjectGroupByIdAndValidateOwner(projectGroupId, userId);
		Project project = findProjectById(projectId);

		projectGroup.removeProject(project);
	}

	/**
	 * 그룹 내 모든 프로젝트 ID 목록 조회
	 */
	@Override
	public List<Long> findAllProjectIdsByGroup(Long userId, Long projectGroupId) {
		ProjectGroup projectGroup = findProjectGroupByIdAndValidateOwner(projectGroupId, userId);
		return projectGroup.getAllProjectIdsInfo();
	}

	/**
	 * 프로젝트 총 개수 조회
	 */
	@Override
	public long getTotalProjectCount(Long userId, Long projectGroupId) {
		ProjectGroup projectGroup = findProjectGroupByIdAndValidateOwner(projectGroupId, userId);
		return projectGroup.getTotalProjectCount();
	}

	/**
	 * 완료된 프로젝트 개수 조회
	 */
	@Override
	public long getCompletedProjectCount(Long userId, Long projectGroupId) {
		ProjectGroup projectGroup = findProjectGroupByIdAndValidateOwner(projectGroupId, userId);
		return (int) projectGroup.getCompletedProjectCount();
	}

	/**
	 * 프로젝트 그룹 생성
	 */
	@Override
	@Transactional
	public Long createGroup(Long userId, ProjectGroupCreateCommand command) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다: " + userId));

		ProjectGroup projectGroup = ProjectGroup.builder()
				.groupName(command.groupName())
				.startDate(command.startDate())
				.endDate(command.endDate())
				.user(user)
				.build();

		ProjectGroup savedProjectGroup = projectGroupRepository.save(projectGroup);

		return savedProjectGroup.getProjectGroupId();
	}

	@Override
	public void deleteGroup(Long userId, Long projectGroupId) {
		// 소유권 확인 후 삭제
		ProjectGroup projectGroup = findProjectGroupByIdAndValidateOwner(projectGroupId, userId);
		projectGroupRepository.delete(projectGroup);
	}

	/**
	 * 프로젝트 그룹 요약 목록 조회
	 */
	@Override
	public List<ProjectGroupSummaryInfo> getAllGroupSummaries(Long userId) {
		return projectGroupRepository.findAllByUser_UserId(userId).stream()
				.map(ProjectGroupSummaryInfo::from)
				.toList();
	}

	/**
	 * 프로젝트 그룹 상세 조회
	 */
	@Override
	public ProjectGroupDetailInfo getGroupDetail(Long userId, Long projectGroupId) {
		ProjectGroup projectGroup = findProjectGroupByIdAndValidateOwner(projectGroupId, userId);
		return ProjectGroupDetailInfo.form(projectGroup);
	}

	/**
	 * 헬퍼 메서드: 그룹 조회 및 소유권 검증
	 */
	private ProjectGroup findProjectGroupByIdAndValidateOwner(Long groupId, Long userId) {
		return projectGroupRepository.findByProjectGroupIdAndUser_UserId(groupId, userId)
				.orElseThrow(() -> new IllegalArgumentException("해당 그룹을 찾을 수 없거나 접근 권한이 없습니다. ID: " + groupId));
	}

	private Project findProjectById(Long id) {
		return projectRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("해당 ID의 프로젝트가 존재하지 않습니다. ID: " + id));
	}
}
