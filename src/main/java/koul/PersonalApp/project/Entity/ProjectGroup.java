package koul.PersonalApp.project.Entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import koul.PersonalApp.global.entity.BaseTimeEntity;
import koul.PersonalApp.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class ProjectGroup extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long projectGroupId; // 프로젝트그룹 아이디

	private String groupName; // 프로젝트 그룹 명

	private LocalDateTime startDate; // 시작일

	private LocalDateTime endDate; // 종료일

	@OneToMany(mappedBy = "projectGroup")
	private List<Project> projectList = new ArrayList<>(); // 포함된 프로젝트

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Enumerated(EnumType.STRING)
	private ProjectGroupStatus status = ProjectGroupStatus.IN_PROGRESS; // 진행 상태 (완료/종료 등)

	@Builder
	public ProjectGroup(String groupName, LocalDateTime startDate, LocalDateTime endDate, ProjectGroupStatus status,
			User user) {
		this.groupName = groupName;
		this.startDate = startDate;
		this.endDate = endDate;
		this.status = (status != null) ? status : ProjectGroupStatus.IN_PROGRESS;
		this.user = user;
	}

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
