package koul.PersonalApp.project.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import koul.PersonalApp.project.Entity.Project;
import koul.PersonalApp.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;

/**
 * 프로젝트 시간 관리(예측/소요 시간)를 전담하는 서비스 구현체
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ProjectTimeServiceImpl implements ProjectTimeService {

	private final ProjectRepository projectRepository;

	/**
	 * 특정 프로젝트에 대한 예측 시간을 설정하거나 수정함
	 * - 이미 시간이 설정되어 있다면 값을 덮어씌움
	 */
	@Override
	public void updatePredictedTime(Long projectId, Long predictedTime) {
		Project project = findProject(projectId);
		// 엔티티에게 상태 변경을 위임 (도메인 주도 설계)
		project.updatePredictedTime(predictedTime);
	}

	/**
	 * 설정된 예측 시간을 초기화(삭제)함
	 * - null 값을 전달하여 시간을 제거하는 방식 사용
	 */
	@Override
	public void deletePredictedTime(Long projectId) {
		Project project = findProject(projectId);
		// 시간을 null로 설정하여 "삭제" 상태를 표현
		project.updatePredictedTime(null);
	}

	/**
	 * 실제 프로젝트 수행에 소요된 시간을 기록하거나 수정함
	 */
	@Override
	public void updateActualTime(Long projectId, Long actualTime) {
		Project project = findProject(projectId);
		project.updateActualTime(actualTime);
	}

	/**
	 * 기록된 소요 시간을 초기화(삭제)함
	 */
	@Override
	public void deleteActualTime(Long projectId) {
		Project project = findProject(projectId);
		project.updateActualTime(null);
	}

	/**
	 * ID로 프로젝트를 조회하는 헬퍼 메서드
	 */
	private Project findProject(Long projectId) {
		return projectRepository.findById(projectId)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프로젝트입니다. ID: " + projectId));
	}
}
