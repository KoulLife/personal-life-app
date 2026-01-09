package koul.PersonalApp.project.service;

import java.util.List;

import koul.PersonalApp.project.dto.ProjectGroupCreateCommand;
import koul.PersonalApp.project.dto.ProjectGroupDetailInfo;
import koul.PersonalApp.project.dto.ProjectGroupSummaryInfo;

public interface ProjectGroupService {
	/**
	 * 그룹에 project 추가하기
	 */
	void addProjectToGroup(Long userId, Long projectGroupId, Long projectId);

	/**
	 * 그룹에 포함 된 project 제거하기
	 */
	void removeProjectFromGroup(Long userId, Long projectGroupId, Long projectId);

	/**
	 * 그룹에 포함된 모든 project id 조회
	 */
	List<Long> findAllProjectIdsByGroup(Long userId, Long projectGroupId);

	/**
	 * project 총 개수 조회
	 */
	long getTotalProjectCount(Long userId, Long projectGroupId);

	/**
	 * 완료 상태의 project 개수 조회
	 */
	long getCompletedProjectCount(Long userId, Long projectGroupId);

	/**
	 * 그룹 생성하기
	 */
	Long createGroup(Long userId, ProjectGroupCreateCommand command);

	/**
	 * 그룹 삭제하기
	 */
	void deleteGroup(Long userId, Long projectGroupId);

	/**
	 * 프로젝트 그룹 요약 목록 조회 (Simple View)
	 */
	List<ProjectGroupSummaryInfo> getAllGroupSummaries(Long userId);

	/**
	 * 프로젝트 그룹 상세 조회 (Detailed View)
	 */
	ProjectGroupDetailInfo getGroupDetail(Long userId, Long projectGroupId);

}
