package koul.PersonalApp.ai.tool;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import koul.PersonalApp.global.security.CustomUserDetails;
import koul.PersonalApp.project.dto.ProjectCreateCommand;
import koul.PersonalApp.project.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectPlanningTool {

    private final ProjectService projectService;

    @Tool(description = "주어진 프로젝트 그룹 내에 연결된 여러 개의 세부 프로젝트들을 순서대로 생성합니다. " +
            "로드맵이나 단계별 계획을 생성할 때 사용합니다.")
    public String createLinkedProjects(
            @ToolParam(description = "프로젝트들이 속할 그룹 ID") Long groupId,
            @ToolParam(description = "순서대로 생성할 프로젝트들의 내용 목록") List<String> projectContents) {

        if (projectContents == null || projectContents.isEmpty()) {
            return "생성할 프로젝트 내용이 없습니다.";
        }

        try {
            CustomUserDetails principal = (CustomUserDetails) SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal();
            Long userId = principal.getUserId();

            // 서비스 호출 (트랜잭션 처리는 서비스 내부에서 수행)
            List<Long> createdProjectIds = projectService.createLinkedProjects(userId, groupId, projectContents);

            return "총 " + createdProjectIds.size() + "개의 연결된 프로젝트가 생성되었습니다. (IDs: " + createdProjectIds + ")";

        } catch (Exception e) {
            log.error("연결된 프로젝트 생성 중 오류 발생", e);

            return "프로젝트 생성 실패: " + e.getMessage();
        }
    }
}
