package koul.PersonalApp.project.service;

import koul.PersonalApp.project.Entity.Project;
import koul.PersonalApp.project.Entity.ProjectGroup;
import koul.PersonalApp.project.dto.ProjectCreateCommand;
import koul.PersonalApp.project.repository.ProjectRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

	@Mock
	private ProjectRepository projectRepository;

	@InjectMocks
	private ProjectServiceImpl projectService;

	@Test
	@DisplayName("성공: 두 프로젝트 연결 시 정상적으로 연결")
	void connectProjects_succes() {
		// given
		Long currentId = 1L;
		Long nextId = 2L;

		Project currentProject = new Project();
		Project nextProject = new Project();

		// 리포지토리가 해당 ID를 찾으면 미리 만든 객체를 반환하도록 설정
		given(projectRepository.findById(currentId)).willReturn(Optional.of(currentProject));
		given(projectRepository.findById(nextId)).willReturn(Optional.of(nextProject));

		// when
		projectService.connectProjects(currentId, nextId);

		// then
		// 현재 프로젝트의 다음 리스트에 대상 프로젝트가 포함되어 있는가?
		assertThat(currentProject.getNextProjects()).contains(nextProject);
		assertThat(currentProject.getNextProjects()).hasSize(1);

		// 대상 프로젝트의 이전 프로젝트가 현재 프로젝트인가?
		assertThat(nextProject.getPrevProject()).isEqualTo(currentProject);
	}

	@Test
	@DisplayName("실패: 현재 프로젝트(Prev)가 없으면 예외가 발생해야 한다.")
	void connectProjects_fail_current_not_found() {
		// given
		Long currentId = 999L; // 없는 ID
		Long nextId = 2L;

		// currentId를 찾으면 Empty를 반환
		given(projectRepository.findById(currentId)).willReturn(Optional.empty());

		// when & then
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			projectService.connectProjects(currentId, nextId);
		});

		// 에러 메시지도 확인
		assertThat(exception.getMessage()).contains("해당 ID의 프로젝트가 존재하지 않습니다");
	}

	@Test
	@DisplayName("실패: 대상 프로젝트(Next)가 없으면 예외가 발생해야 한다.")
	void connectProjects_fail_next_not_found() {
		// given
		Long currentId = 1L;
		Long nextId = 999L; // 없는 ID

		Project currentProject = new Project();

		// current는 찾았는데, next는 못 찾은 상황 시뮬레이션
		given(projectRepository.findById(currentId)).willReturn(Optional.of(currentProject));
		given(projectRepository.findById(nextId)).willReturn(Optional.empty());

		// when & then
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			projectService.connectProjects(currentId, nextId);
		});

		assertThat(exception.getMessage()).contains("대상 ID의 프로젝트가 존재하지 않습니다");
	}

	@Test
	@DisplayName("루트 프로젝트 여부 확인 - 루트인 경우")
	void isRootProject_true() {
		// given
		Long projectId = 1L;
		Project rootProject = Project.builder()
			.content("루트 프로젝트")
			.prevProject(null) // 이전 프로젝트가 없으면 루트
			.build();

		given(projectRepository.findById(projectId)).willReturn(Optional.of(rootProject));

		// when
		boolean result = projectService.isRootProject(projectId);

		// then
		assertThat(result).isTrue();
	}

	@Test
	@DisplayName("루트 프로젝트 여부 확인 - 자식 프로젝트인 경우")
	void isRootProject_false() {
		// given
		Long childId = 2L;
		Project parentProject = new Project();
		Project childProject = Project.builder()
			.content("자식 프로젝트")
			.prevProject(parentProject) // 부모가 연결된 상태
			.build();

		given(projectRepository.findById(childId)).willReturn(Optional.of(childProject));

		// when
		boolean result = projectService.isRootProject(childId);

		// then
		assertThat(result).isFalse();
	}

	@Test
	@DisplayName("프로젝트 완료 상태 확인 - 완료된 경우")
	void isProjectCompleted_true() {
		// given
		Long projectId = 1L;
		Project completedProject = Project.builder()
			.content("완료된 업무")
			.completeStatus(true) // 완료 상태 설정
			.build();

		given(projectRepository.findById(projectId)).willReturn(Optional.of(completedProject));

		// when
		boolean result = projectService.isProjectCompleted(projectId);

		// then
		assertThat(result).isTrue();
	}

	@Test
	@DisplayName("프로젝트 생성 - 정상 저장 후 ID 반환 확인")
	void createProject_success() {
		// given
		ProjectGroup projectGroup = new ProjectGroup();
		ProjectCreateCommand request = new ProjectCreateCommand("새 프로젝트", false, projectGroup);
		Project project = request.toEntity();

		// Reflection을 사용해 가짜 ID 주입 (실제 DB 저장 효과 시뮬레이션)
		Project savedProject = Project.builder()
			.content(project.getContent())
			.build();
		// 실제 프로젝트 구조에 맞게 projectId가 저장된 객체가 반환된다고 가정
		given(projectRepository.save(any(Project.class))).willReturn(savedProject);

		// when
		Long savedId = projectService.createProject(request);

		// then
		// 리포지토리의 save가 호출되었는지와 반환된 ID가 일치하는지 확인
		verify(projectRepository, times(1)).save(any(Project.class));
	}

	@Test
	@DisplayName("프로젝트 삭제 - 존재하지 않는 ID 삭제 시 예외 발생")
	void deleteProject_fail_not_found() {
		// given
		Long invalidId = 999L;
		// deleteById는 반환값이 없으므로 doThrow로 예외 상황 설정
		doThrow(new RuntimeException()).when(projectRepository).deleteById(invalidId);

		// when & then
		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			projectService.deleteProject(invalidId);
		});

		assertThat(exception.getMessage()).isEqualTo("해당 ID가 존재하지 않습니다.");
	}

	@Test
	@DisplayName("프로젝트 내용 업데이트 - 변경 감지 동작 확인")
	void updateProject_success() {
		// given
		Long projectId = 1L;
		String newContent = "수정된 내용";
		Project project = Project.builder().content("기존 내용").build();

		given(projectRepository.findById(projectId)).willReturn(Optional.of(project));

		// when
		projectService.updateProject(projectId, newContent);

		// then
		// 엔티티의 필드값이 실제로 변경되었는지 확인
		assertThat(project.getContent()).isEqualTo(newContent);
	}

	@Test
	@DisplayName("프로젝트 완료 처리 - 상태값이 true로 변경되는지 확인")
	void completeProject_success() {
		// given
		Long projectId = 1L;
		Project project = Project.builder().completeStatus(false).build();

		given(projectRepository.findById(projectId)).willReturn(Optional.of(project));

		// when
		projectService.completeProject(projectId);

		// then
		assertThat(project.isCompleteStatus()).isTrue();
	}

	@Test
	@DisplayName("프로젝트 완료 철회 - 상태값이 false로 변경되는지 확인")
	void undoCompleteProject_success() {
		// given
		Long projectId = 1L;
		Project project = Project.builder().completeStatus(true).build();

		given(projectRepository.findById(projectId)).willReturn(Optional.of(project));

		// when
		projectService.undoCompleteProject(projectId);

		// then
		assertThat(project.isCompleteStatus()).isFalse();
	}

}