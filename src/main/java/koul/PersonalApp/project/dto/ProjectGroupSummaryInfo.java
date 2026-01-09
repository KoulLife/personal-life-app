package koul.PersonalApp.project.dto;

import java.time.LocalDateTime;

import koul.PersonalApp.project.Entity.ProjectGroup;
import koul.PersonalApp.project.Entity.ProjectGroupStatus;

public record ProjectGroupSummaryInfo(
        Long projectGroupId,
        String groupName,
        LocalDateTime startDate,
        LocalDateTime endDate,
        ProjectGroupStatus status,
        long totalProjectCount,
        long completedProjectCount) {
    public static ProjectGroupSummaryInfo from(ProjectGroup group) {
        return new ProjectGroupSummaryInfo(
                group.getProjectGroupId(),
                group.getGroupName(),
                group.getStartDate(),
                group.getEndDate(),
                group.getStatus(),
                group.getTotalProjectCount(),
                group.getCompletedProjectCount());
    }
}
