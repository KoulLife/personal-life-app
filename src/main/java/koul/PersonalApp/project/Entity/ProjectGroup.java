package koul.PersonalApp.project.Entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class ProjectGroup {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long projectGroupId;	// 프로젝트그룹 아이디

	@OneToMany(mappedBy = "projectGroup")
	private List<Project> projectList = new ArrayList<>();	// 포함된 프로젝트

	/**
	 * 그룹에 project 추가
	 */
	public void addProject(Project project) {
		projectList.add(project);
		project.assignGroup(this);
	}

	/**
	 * 그룹에 포함 된 project 제거하기
	 */
	public void removeProject(Project project) {
		project.initProjectGroup();
		projectList.remove(project);
	}

	/**
	 * 그룹에 포함된 모든 project id 정보 반환
	 */
	public List<Long> getAllProjectIdsInfo() {
		return projectList.stream()
			.map(Project::getProjectId) // Project 객체에서 ID만 추출
			.toList();
	}

	/**
	 * project 총 개수 조회
	 */
	public long getTotalProjectCount() {
		return projectList.size();
	}

	/**
	 * 완료 상태의 project 개수 조회
	 */
	public long getCompletedProjectCount() {
		return projectList.stream()
			.filter(Project::isCompleteStatus) // 완료된 프로젝트만 필터링
			.count();
	}


}
