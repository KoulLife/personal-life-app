package koul.PersonalApp.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import koul.PersonalApp.global.security.CustomUserDetails;
import koul.PersonalApp.project.Entity.ProjectGroupStatus;
import koul.PersonalApp.project.dto.ProjectGroupCreateCommand;
import koul.PersonalApp.project.dto.ProjectGroupDetailInfo;
import koul.PersonalApp.project.dto.ProjectGroupRequest;
import koul.PersonalApp.project.dto.ProjectGroupSummaryInfo;
import koul.PersonalApp.project.service.ProjectGroupService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/project-group")
@RequiredArgsConstructor
public class ProjectGroupController {

	public final ProjectGroupService projectGroupService;

	@PostMapping
	public ResponseEntity<String> createProjectGroup(
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@RequestBody ProjectGroupRequest request) {
		ProjectGroupCreateCommand projectGroupCreateCommand = ProjectGroupCreateCommand.builder()
				.groupName(request.groupName())
				.startDate(request.startDate())
				.endDate(request.endDate())
				.status(ProjectGroupStatus.IN_PROGRESS)
				.build();

		projectGroupService.createGroup(userDetails.getUserId(), projectGroupCreateCommand);
		return ResponseEntity.ok("성공적으로 프로젝트 그룹이 생성되었습니다.");
	}

	@PostMapping("/{projectGroupId}/projects/{projectId}")
	public ResponseEntity<String> addProjectToGroup(
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@PathVariable("projectGroupId") Long projectGroupId,
			@PathVariable("projectId") Long projectId) {
		projectGroupService.addProjectToGroup(userDetails.getUserId(), projectGroupId, projectId);
		return ResponseEntity.ok("프로젝트가 그룹에 성공적으로 추가되었습니다.");
	}

	@DeleteMapping("/{projectGroupId}/projects/{projectId}")
	public ResponseEntity<String> removeProjectFromGroup(
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@PathVariable("projectGroupId") Long projectGroupId,
			@PathVariable("projectId") Long projectId) {
		projectGroupService.removeProjectFromGroup(userDetails.getUserId(), projectGroupId, projectId);
		return ResponseEntity.ok("프로젝트가 그룹에서 성공적으로 제거되었습니다.");
	}

	@DeleteMapping("/{projectGroupId}")
	public ResponseEntity<String> deleteGroup(
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@PathVariable("projectGroupId") Long projectGroupId) {
		projectGroupService.deleteGroup(userDetails.getUserId(), projectGroupId);
		return ResponseEntity.ok("프로젝트 그룹이 성공적으로 삭제되었습니다.");
	}

	@GetMapping("/{projectGroupId}/projects")
	public ResponseEntity<List<Long>> getProjectsInGroup(
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@PathVariable("projectGroupId") Long projectGroupId) {
		List<Long> projectIds = projectGroupService.findAllProjectIdsByGroup(userDetails.getUserId(), projectGroupId);
		return ResponseEntity.ok(projectIds);
	}

	@GetMapping
	public ResponseEntity<List<ProjectGroupSummaryInfo>> getGroupSummaries(
			@AuthenticationPrincipal CustomUserDetails userDetails) {
		List<ProjectGroupSummaryInfo> summaries = projectGroupService.getAllGroupSummaries(userDetails.getUserId());
		return ResponseEntity.ok(summaries);
	}

	@GetMapping("/{projectGroupId}")
	public ResponseEntity<ProjectGroupDetailInfo> getGroupDetail(
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@PathVariable("projectGroupId") Long projectGroupId) {
		ProjectGroupDetailInfo detail = projectGroupService.getGroupDetail(userDetails.getUserId(), projectGroupId);
		return ResponseEntity.ok(detail);
	}

}
