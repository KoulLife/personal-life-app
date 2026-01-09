package koul.PersonalApp.project.Entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
public class Project extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long projectId; // 프로젝트 ID

	private String content; // 프로젝트 내용

	private boolean completeStatus = false; // 프로젝트 완료 상태

	@Embedded
	private ProjectTime projectTime; // 프로젝트 시간 정보

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "prev_project_id")
	private Project prevProject; // 이전(부모) 프로젝트

	@OneToMany(mappedBy = "prevProject", cascade = CascadeType.ALL)
	private List<Project> nextProjects = new ArrayList<>(); // 다음(자손) 프로젝트

	@ManyToOne
	@JoinColumn(name = "project_group_id")
	private ProjectGroup projectGroup; // 프로젝트 그룹

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user; // 프로젝트 주인

	@Builder
	public Project(String content, boolean completeStatus, Project prevProject, List<Project> nextProjects,
			ProjectGroup projectGroup, ProjectTime projectTime, User user) {
		this.content = content;
		this.completeStatus = completeStatus;
		this.prevProject = prevProject;
		this.nextProjects = nextProjects;
		this.projectGroup = projectGroup;
		this.projectTime = projectTime;
		this.user = user;
	}

	/**
	 * 이전 프로젝트 연결
	 */
	public void connectToPrevProject(Project prevProject) {
		this.prevProject = prevProject;
	}

	/**
	 * 다음 프로젝트 연결
	 */
	public void connectToNextProject(Project nextProject) {
		this.nextProjects.add(nextProject);
	}

	/**
	 * 프로젝트 내용 수정
	 */
	public void changeContent(String content) {
		this.content = content;
	}

	/**
	 * 루트 프로젝트인지 체크
	 */
	public boolean isRootProject() {
		if (prevProject == null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 프로젝트 완성 유무 변경 참/거짓으로
	 */
	public void changeCompleteStatus(boolean status) {
		completeStatus = status;
	}

	/**
	 * 프로젝트의 그룹 설정
	 */
	public void assignGroup(ProjectGroup group) {
		this.projectGroup = group;
	}

	/**
	 * 프로젝트의 그룹 설정
	 */
	public void initProjectGroup() {
		this.projectGroup = null;
	}

	/**
	 * 프로젝트 예측 시간을 설정하거나 수정함
	 * ProjectTime이 없으면 새로 생성하고, 있으면 기존 actualTime은 유지한 채 predictedTime만 변경
	 */
	public void updatePredictedTime(Long predictedTime) {
		if (this.projectTime == null) {
			this.projectTime = ProjectTime.builder()
					.predictedTime(predictedTime)
					.build();
		} else {
			this.projectTime = ProjectTime.builder()
					.predictedTime(predictedTime)
					.actualTime(this.projectTime.getActualTime())
					.build();
		}
	}

	/**
	 * 프로젝트 실제 소요 시간을 기록하거나 수정함
	 * ProjectTime이 없으면 새로 생성하고, 있으면 기존 predictedTime은 유지한 채 actualTime만 변경
	 */
	public void updateActualTime(Long actualTime) {
		if (this.projectTime == null) {
			this.projectTime = ProjectTime.builder()
					.actualTime(actualTime)
					.build();
		} else {
			this.projectTime = ProjectTime.builder()
					.predictedTime(this.projectTime.getPredictedTime())
					.actualTime(actualTime)
					.build();
		}
	}

	/**
	 * 프로젝트의 유저 설정
	 */
	public void assignUser(User user) {
		this.user = user;
	}

}
