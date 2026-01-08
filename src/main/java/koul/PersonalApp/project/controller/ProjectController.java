package koul.PersonalApp.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

}
