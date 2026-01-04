package koul.PersonalApp.project.service;

import java.util.List;

public interface ProjectGroupService {
	/**
	 * 그룹에 project 추가하기
	 */
	void addProjectToGroup(Long projectGroupId, Long projectId);

	/**
	 * 그룹에 포함 된 project 제거하기
	 */
	void removeProjectFromGroup(Long projectGroupId, Long projectId);

	/**
	 * 그룹에 포함된 모든 project id 조회
	 */
	List<Long> findAllProjectIdsByGroup(Long projectGroupId);

	/**
	 * project 총 개수 조회
	 */
	long getTotalProjectCount(Long projectGroupId);

	/**
	 * 완료 상태의 project 개수 조회
	 */
	long getCompletedProjectCount(Long projectGroupId);

	/**
	 * 그룹 생성하기
	 */
	Long createGroup();

	/**
	 * 그룹 삭제하기
	 */
	void deleteGroup(Long projectGroupId);


}
