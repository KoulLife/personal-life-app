package koul.PersonalApp.project.dto;

import java.time.LocalDateTime;

import koul.PersonalApp.project.Entity.ProjectGroupStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 프로젝트 그룹 생성 요청을 전달하는 커맨드 객체
 * 서비스 계층에서 사용됨
 */
@Builder
public record ProjectGroupCreateCommand (
    String groupName,
    LocalDateTime startDate,
    LocalDateTime endDate,
    ProjectGroupStatus status){
}
