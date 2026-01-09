package koul.PersonalApp.project.dto;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record ProjectGroupRequest(
		String groupName,
		LocalDateTime startDate,
		LocalDateTime endDate) {
}
