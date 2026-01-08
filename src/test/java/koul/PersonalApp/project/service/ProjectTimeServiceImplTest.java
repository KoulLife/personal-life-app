package koul.PersonalApp.project.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import koul.PersonalApp.project.Entity.Project;
import koul.PersonalApp.project.repository.ProjectRepository;

@ExtendWith(MockitoExtension.class)
class ProjectTimeServiceImplTest {

    @InjectMocks
    private ProjectTimeServiceImpl projectTimeService;

    @Mock
    private ProjectRepository projectRepository;

    @Test
    @DisplayName("예측 시간을 설정/수정한다.")
    void updatePredictedTime() {
        // given
        Long projectId = 1L;
        Long predictedTime = 3600L; // 1시간 (초 단위)
        Project project = Project.builder().build(); // 빈 프로젝트 생성

        given(projectRepository.findById(projectId)).willReturn(Optional.of(project));

        // when
        projectTimeService.updatePredictedTime(projectId, predictedTime);

        // then
        assertThat(project.getProjectTime()).isNotNull();
        assertThat(project.getProjectTime().getPredictedTime()).isEqualTo(predictedTime);
    }

    @Test
    @DisplayName("예측 시간을 삭제한다.")
    void deletePredictedTime() {
        // given
        Long projectId = 1L;
        Project project = Project.builder().build();
        // 시간 설정
        project.updatePredictedTime(3600L);

        given(projectRepository.findById(projectId)).willReturn(Optional.of(project));

        // when
        projectTimeService.deletePredictedTime(projectId);

        // then
        assertThat(project.getProjectTime()).isNotNull();
        assertThat(project.getProjectTime().getPredictedTime()).isNull();
    }

    @Test
    @DisplayName("실제 소요 시간을 기록/수정한다.")
    void updateActualTime() {
        // given
        Long projectId = 1L;
        Long actualTime = 7200L; // 2시간
        Project project = Project.builder().build();

        given(projectRepository.findById(projectId)).willReturn(Optional.of(project));

        // when
        projectTimeService.updateActualTime(projectId, actualTime);

        // then
        assertThat(project.getProjectTime()).isNotNull();
        assertThat(project.getProjectTime().getActualTime()).isEqualTo(actualTime);
    }

    @Test
    @DisplayName("실제 소요 시간을 삭제한다.")
    void deleteActualTime() {
        // given
        Long projectId = 1L;
        Project project = Project.builder().build();
        project.updateActualTime(7200L);

        given(projectRepository.findById(projectId)).willReturn(Optional.of(project));

        // when
        projectTimeService.deleteActualTime(projectId);

        // then
        assertThat(project.getProjectTime()).isNotNull();
        assertThat(project.getProjectTime().getActualTime()).isNull();
    }

    @Test
    @DisplayName("예측 시간이 있는 상태에서 실제 시간을 업데이트해도 예측 시간은 유지된다.")
    void keepPredictedTimeWhenUpdatingActualTime() {
        // given
        Long projectId = 1L;
        Long predictedTime = 3600L;
        Long actualTime = 4000L;
        Project project = Project.builder().build();

        given(projectRepository.findById(projectId)).willReturn(Optional.of(project));

        // when
        projectTimeService.updatePredictedTime(projectId, predictedTime);
        projectTimeService.updateActualTime(projectId, actualTime);

        // then
        assertThat(project.getProjectTime().getPredictedTime()).isEqualTo(predictedTime);
        assertThat(project.getProjectTime().getActualTime()).isEqualTo(actualTime);
    }

    @Test
    @DisplayName("존재하지 않는 프로젝트 ID로 요청 시 예외가 발생한다.")
    void throwExceptionWhenProjectNotFound() {
        // given
        Long projectId = 999L;
        given(projectRepository.findById(projectId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> projectTimeService.updatePredictedTime(projectId, 100L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 프로젝트입니다");
    }
}