package koul.PersonalApp.project.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 프로젝트의 시간 관련 정보 (예측 시간, 실제 소요 시간)를 관리하는 내장 타입
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectTime {

    private Long predictedTime; // 예측 소요 시간 (초 단위)
    private Long actualTime; // 실제 소요 시간

    @Builder
    public ProjectTime(Long predictedTime, Long actualTime) {
        this.predictedTime = predictedTime;
        this.actualTime = actualTime;
    }
}
