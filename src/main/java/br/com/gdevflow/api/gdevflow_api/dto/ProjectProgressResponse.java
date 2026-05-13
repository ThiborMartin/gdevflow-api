package br.com.gdevflow.api.gdevflow_api.dto;

import java.util.List;

import br.com.gdevflow.api.gdevflow_api.model.ProjectStatus;

public record ProjectProgressResponse(
        Long projectId,
        String projectName,
        String projectDescription,
        ProjectStatus projectStatus,
        UserSummaryResponse freelancer,
        int progressPercentage,
        long totalSprints,
        long totalTasks,
        long todoTasks,
        long inProgressTasks,
        long doneTasks,
        long blockedTasks,
        List<SprintProgressResponse> sprints) {
}
