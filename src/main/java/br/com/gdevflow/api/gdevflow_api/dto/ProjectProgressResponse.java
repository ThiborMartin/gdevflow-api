package br.com.gdevflow.api.gdevflow_api.dto;

import java.util.List;

public record ProjectProgressResponse(
        Long projectId,
        String projectName,
        int progressPercentage,
        long totalSprints,
        long totalTasks,
        long todoTasks,
        long inProgressTasks,
        long doneTasks,
        long blockedTasks,
        List<SprintProgressResponse> sprints) {
}
