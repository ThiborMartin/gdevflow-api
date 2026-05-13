package br.com.gdevflow.api.gdevflow_api.dto;

import java.time.LocalDateTime;
import java.util.List;

import br.com.gdevflow.api.gdevflow_api.model.ProjectStatus;

public record ProjectProgressResponse(
        Long projectId,
        String projectName,
        String projectDescription,
        ProjectStatus projectStatus,
        LocalDateTime completedAt,
        UserSummaryResponse freelancer,
        UserSummaryResponse client,
        int progressPercentage,
        long totalSprints,
        long totalTasks,
        long todoTasks,
        long inProgressTasks,
        long doneTasks,
        long blockedTasks,
        List<SprintProgressResponse> sprints) {
}
