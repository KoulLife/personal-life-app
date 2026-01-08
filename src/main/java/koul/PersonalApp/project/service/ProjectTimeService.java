package koul.PersonalApp.project.service;

public interface ProjectTimeService {

	// 프로젝트 예측 시간 수정 (생성 포함)
	void updatePredictedTime(Long projectId, Long predictedTime);

	// 프로젝트 예측 시간 삭제
	void deletePredictedTime(Long projectId);

	// 프로젝트 실제 소요 시간 수정 (기록 포함)
	void updateActualTime(Long projectId, Long actualTime);

	// 프로젝트 실제 소요 시간 삭제
	void deleteActualTime(Long projectId);
}
