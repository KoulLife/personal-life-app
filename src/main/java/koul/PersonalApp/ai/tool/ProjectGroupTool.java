package koul.PersonalApp.ai.tool;

import java.time.LocalDateTime;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import koul.PersonalApp.global.security.CustomUserDetails;
import koul.PersonalApp.project.Entity.ProjectGroupStatus;
import koul.PersonalApp.project.dto.ProjectGroupCreateCommand;
import koul.PersonalApp.project.service.ProjectGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectGroupTool {

    private final ProjectGroupService projectGroupService;

    @Tool(description = "새로운 프로젝트 그룹을 생성합니다. 사용자가 프로젝트 그룹 생성을 요청할 때 사용합니다.")
    public String createProjectGroup(
            @ToolParam(description = "생성할 프로젝트 그룹의 이름") String name) {

        try {
            // 현재 로그인한 사용자 정보 가져오기
            CustomUserDetails principal = (CustomUserDetails) SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal();
            Long userId = principal.getUserId();

            // 커맨드 객체 생성 (기본값 설정)
            ProjectGroupCreateCommand command = ProjectGroupCreateCommand.builder()
                    .groupName(name)
                    .startDate(LocalDateTime.now())
                    .status(ProjectGroupStatus.IN_PROGRESS)
                    .build();

            // 서비스 호출
            Long groupId = projectGroupService.createGroup(userId, command);

            return "성공적으로 프로젝트 그룹 '" + name + "'을(를) 생성했습니다. (Group ID: " + groupId + ")";
        } catch (Exception e) {
            log.error("프로젝트 그룹 생성 중 오류 발생", e);
            return "프로젝트 그룹 생성에 실패했습니다: " + e.getMessage();
        }
    }
}
