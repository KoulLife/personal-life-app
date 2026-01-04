package koul.PersonalApp.project.Entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Project {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long projectId;	// 프로젝트 ID

	private String content;	// 프로젝트 내용

	private boolean completeStatus = false;	// 프로젝트 완료 상태

	@ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "prev_project_id")
	private Project prevProject;	// 이전(부모) 프로젝트

	@OneToMany(mappedBy = "prevProject", cascade = CascadeType.ALL)
	private List<Project> nextProjects = new ArrayList<>();	// 다음(자손) 프로젝트

	@Builder
	public Project(String content, boolean completeStatus, Project prevProject, List<Project> nextProjects) {
		this.content = content;
		this.completeStatus = completeStatus;
		this.prevProject = prevProject;
		this.nextProjects = nextProjects;
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
		if(prevProject == null) {
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

}
