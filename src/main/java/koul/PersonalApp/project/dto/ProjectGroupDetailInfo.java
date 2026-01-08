package koul.PersonalApp.project.dto;

import java.time.LocalDateTime;
import java.util.List;

import koul.PersonalApp.project.Entity.ProjectGroup;
import koul.PersonalApp.project.Entity.ProjectGroupStatus;

public record ProjectGroupDetailInfo(
        Long projectGroupId,
        String groupName,
        LocalDateTime startDate,
        LocalDateTime endDate,
        ProjectGroupStatus status,
        long totalProjectCount,
        long completedProjectCount,
        List<ProjectInfo> projects) {
    public static ProjectGroupDetailInfo form(ProjectGroup group) {
        List<ProjectInfo> projectInfos = group.getProjectList().stream()
                .map(ProjectInfo::from)
                .toList();

        return new ProjectGroupDetailInfo(
                group.getProjectGroupId(),
                group.getGroupName(),
                group.getStartDate(),
                group.getEndDate(),
                group.getStatus(),
                group.getTotalProjectCount(),
                group.getCompletedProjectCount(),
                projectInfos);
    }
}
