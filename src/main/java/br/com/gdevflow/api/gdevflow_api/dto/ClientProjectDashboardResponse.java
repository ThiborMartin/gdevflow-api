package br.com.gdevflow.api.gdevflow_api.dto;

import java.time.LocalDateTime;

import br.com.gdevflow.api.gdevflow_api.model.Project;
import br.com.gdevflow.api.gdevflow_api.model.ProjectStatus;

public record ClientProjectDashboardResponse(
        Long id,
        String name,
        String description,
        LocalDateTime createdAt,
        ProjectStatus status,
        UserSummaryResponse owner,
        int progressPercentage,
        long totalSprints,
        long totalTasks,
        long doneTasks) {

    public static ClientProjectDashboardResponse fromEntity(
            Project project,
            long totalSprints,
            long totalTasks,
            long doneTasks) {
        int progressPercentage = totalTasks == 0
                ? 0
                : (int) Math.round((doneTasks * 100.0) / totalTasks);

        return new ClientProjectDashboardResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getCreatedAt(),
                project.getStatus(),
                UserSummaryResponse.fromEntity(project.getOwner()),
                progressPercentage,
                totalSprints,
                totalTasks,
                doneTasks);
    }
}
