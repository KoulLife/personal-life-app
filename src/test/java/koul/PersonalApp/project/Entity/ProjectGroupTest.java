package koul.PersonalApp.project.Entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class ProjectGroupTest {

	@Test
	@DisplayName("프로젝트 추가 시 양방향 연관관계가 설정되어야 한다")
	void addProject_success() {
		// given
		ProjectGroup group = new ProjectGroup();
		Project project = Project.builder().content("테스트 프로젝트").build();

		// when
		group.addProject(project);

		// then
		// 1. 그룹 리스트에 프로젝트가 들어갔는지
		assertThat(group.getProjectList()).hasSize(1);
		assertThat(group.getProjectList()).contains(project);

		// 2. 프로젝트에도 그룹이 잘 세팅되었는지 (연관관계 편의 메서드 동작 확인)
		assertThat(project.getProjectGroup()).isEqualTo(group);
	}

	@Test
	@DisplayName("프로젝트 제거 시 양방향 관계가 모두 끊겨야 한다")
	void removeProject_success() {
		// given
		ProjectGroup group = new ProjectGroup();
		Project project = Project.builder().content("삭제할 프로젝트").build();
		group.addProject(project); // 먼저 추가

		// when
		group.removeProject(project);

		// then
		// 리스트에서 빠졌는지 확인
		assertThat(group.getProjectList()).isEmpty();

		// 프로젝트 쪽에서도 그룹 정보가 null로 초기화되었는지 확인
		assertThat(project.getProjectGroup()).isNull();
	}

	@Test
	@DisplayName("통계 메서드 검증 (총 개수 / 완료된 개수)")
	void statistics_test() {
		// given
		ProjectGroup group = new ProjectGroup();

		// 완료된 것 2개, 미완료 1개 생성
		Project p1 = Project.builder().completeStatus(true).build();
		Project p2 = Project.builder().completeStatus(true).build();
		Project p3 = Project.builder().completeStatus(false).build();

		group.addProject(p1);
		group.addProject(p2);
		group.addProject(p3);

		// when & then
		// 총 개수는 3개여야 함
		assertThat(group.getTotalProjectCount()).isEqualTo(3);

		// 완료된 프로젝트는 2개여야 함 (Stream 필터링 로직 검증)
		assertThat(group.getCompletedProjectCount()).isEqualTo(2);
	}

	@Test
	@DisplayName("그룹 내 모든 프로젝트 ID 목록 조회")
	void getAllProjectIdsInfo_test() {
		// given
		ProjectGroup group = new ProjectGroup();
		Project p1 = Project.builder().content("P1").build();
		Project p2 = Project.builder().content("P2").build();

		// DB 없이 테스트하니까 ID가 null임. 리플렉션으로 강제 주입해줌.
		setProjectId(p1, 100L);
		setProjectId(p2, 200L);

		group.addProject(p1);
		group.addProject(p2);

		// when
		List<Long> ids = group.getAllProjectIdsInfo();

		// then
		assertThat(ids).hasSize(2);
		assertThat(ids).containsExactly(100L, 200L);
	}

	// 테스트용으로 ID 강제 주입하는 헬퍼 메서드
	private void setProjectId(Project project, Long id) {
		try {
			Field field = Project.class.getDeclaredField("projectId");
			field.setAccessible(true);
			field.set(project, id);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}