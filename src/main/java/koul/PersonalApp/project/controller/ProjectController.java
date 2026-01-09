package koul.PersonalApp.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import koul.PersonalApp.global.security.CustomUserDetails;
import koul.PersonalApp.project.dto.ProjectCreateCommand;
import koul.PersonalApp.project.dto.ProjectRequest;
import koul.PersonalApp.project.service.ProjectService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/project")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<String> createProject(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid ProjectRequest request) {

        ProjectCreateCommand command = ProjectCreateCommand.builder()
                .userId(userDetails.getUserId())
                .projectGroupId(request.projectGroupId())
                .prevProjectId(request.prevProjectId())
                .content(request.content())
                .completeStatus(false) // 기본값 false
                .build();

        projectService.createProject(command);

        return ResponseEntity.ok("성공적으로 프로젝트가 생성되었습니다.");
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<String> deleteProject(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("projectId") Long projectId) {
        projectService.deleteProject(userDetails.getUserId(), projectId);
        return ResponseEntity.ok("성공적으로 프로젝트가 삭제되었습니다.");
    }

    @PatchMapping("/{projectId}")
    public ResponseEntity<String> updateProject(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("projectId") Long projectId,
            @RequestBody String content) {
        projectService.updateProject(userDetails.getUserId(), projectId, content);
        return ResponseEntity.ok("성공적으로 프로젝트가 수정되었습니다.");
    }

    @PostMapping("/{projectId}/complete")
    public ResponseEntity<String> completeProject(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("projectId") Long projectId) {
        projectService.completeProject(userDetails.getUserId(), projectId);
        return ResponseEntity.ok("성공적으로 프로젝트가 완료되었습니다.");
    }

    @PostMapping("/{projectId}/undo-complete")
    public ResponseEntity<String> undoCompleteProject(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("projectId") Long projectId) {
        projectService.undoCompleteProject(userDetails.getUserId(), projectId);
        return ResponseEntity.ok("성공적으로 프로젝트 완료가 철회되었습니다.");
    }

    @PostMapping("/{currentProjectId}/{nextProjectId}/connect")
    public ResponseEntity<String> connectProject(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("currentProjectId") Long currentProjectId,
            @PathVariable("nextProjectId") Long nextProjectId) {
        projectService.connectProjects(userDetails.getUserId(), currentProjectId, nextProjectId);
        return ResponseEntity.ok("성공적으로 프로젝트 연결되었습니다.");
    }

}
