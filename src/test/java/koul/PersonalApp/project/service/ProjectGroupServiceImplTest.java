package koul.PersonalApp.project.service;

import koul.PersonalApp.project.Entity.Project;
import koul.PersonalApp.project.Entity.ProjectGroup;
import koul.PersonalApp.project.repository.ProjectGroupRepository;
import koul.PersonalApp.project.repository.ProjectRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProjectGroupServiceImplTest {

	@Mock
	private ProjectGroupRepository projectGroupRepository;

	@Mock
	private ProjectRepository projectRepository;

	@InjectMocks
	private ProjectGroupServiceImpl projectGroupService;

	@Test
	@DisplayName("그룹 생성 - 정상적으로 생성되고 ID를 반환해야 한다")
	void createGroup_success() {
		// given
		ProjectGroup savedGroup = new ProjectGroup();
		given(projectGroupRepository.save(any(ProjectGroup.class))).willReturn(savedGroup);

		// when
		projectGroupService.createGroup();

		// then
		// save 메서드가 1번 호출되었는지 검증
		verify(projectGroupRepository, times(1)).save(any(ProjectGroup.class));
	}

	@Test
	@DisplayName("그룹에 프로젝트 추가 - 정상 동작")
	void addProjectToGroup_success() {
		// given
		Long groupId = 1L;
		Long projectId = 100L;

		ProjectGroup group = new ProjectGroup();
		Project project = Project.builder().content("테스트 프로젝트").build();

		given(projectGroupRepository.findById(groupId)).willReturn(Optional.of(group));
		given(projectRepository.findById(projectId)).willReturn(Optional.of(project));

		// when
		projectGroupService.addProjectToGroup(groupId, projectId);

		// then
		// 그룹 리스트에 프로젝트가 추가되었는지 확인
		assertThat(group.getProjectList()).contains(project);
		// 프로젝트에도 그룹이 설정되었는지 확인
		assertThat(project.getProjectGroup()).isEqualTo(group);
	}

	@Test
	@DisplayName("그룹에 프로젝트 추가 - 그룹이 없으면 예외 발생")
	void addProjectToGroup_fail_groupNotFound() {
		// given
		Long invalidGroupId = 999L;
		Long projectId = 100L;

		given(projectGroupRepository.findById(invalidGroupId)).willReturn(Optional.empty());

		// when & then
		assertThrows(IllegalArgumentException.class, () ->
			projectGroupService.addProjectToGroup(invalidGroupId, projectId)
		);
	}

	@Test
	@DisplayName("그룹에서 프로젝트 제거 - 정상 동작")
	void removeProjectFromGroup_success() {
		// given
		Long groupId = 1L;
		Long projectId = 100L;

		ProjectGroup group = new ProjectGroup();
		Project project = Project.builder().content("삭제할 프로젝트").build();

		// 미리 추가해둠
		group.addProject(project);

		given(projectGroupRepository.findById(groupId)).willReturn(Optional.of(group));
		given(projectRepository.findById(projectId)).willReturn(Optional.of(project));

		// when
		projectGroupService.removeProjectFromGroup(groupId, projectId);

		// then
		// 리스트에서 제거되었는지 확인
		assertThat(group.getProjectList()).doesNotContain(project);
		// 연관관계가 끊겼는지 확인
		assertThat(project.getProjectGroup()).isNull();
	}

	@Test
	@DisplayName("그룹 삭제 - 존재하는 그룹 삭제 시 리포지토리 호출 확인")
	void deleteGroup_success() {
		// given
		Long groupId = 1L;
		given(projectGroupRepository.existsById(groupId)).willReturn(true);

		// when
		projectGroupService.deleteGroup(groupId);

		// then
		verify(projectGroupRepository, times(1)).deleteById(groupId);
	}

	@Test
	@DisplayName("그룹 삭제 - 존재하지 않는 그룹이면 예외 발생")
	void deleteGroup_fail_notFound() {
		// given
		Long invalidGroupId = 999L;
		given(projectGroupRepository.existsById(invalidGroupId)).willReturn(false);

		// when & then
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
			projectGroupService.deleteGroup(invalidGroupId)
		);
		assertThat(exception.getMessage()).contains("존재하지 않습니다");
	}

	@Test
	@DisplayName("통계 조회 - 완료된 프로젝트 개수 확인")
	void getCompletedProjectCount_success() {
		// given
		Long groupId = 1L;
		ProjectGroup group = new ProjectGroup();

		// 완료된 프로젝트 2개, 미완료 1개 추가
		group.addProject(Project.builder().completeStatus(true).build());
		group.addProject(Project.builder().completeStatus(true).build());
		group.addProject(Project.builder().completeStatus(false).build());

		given(projectGroupRepository.findById(groupId)).willReturn(Optional.of(group));

		// when
		long count = projectGroupService.getCompletedProjectCount(groupId);

		// then
		assertThat(count).isEqualTo(2);
	}
}