package br.com.gdevflow.api.gdevflow_api.dto;

import br.com.gdevflow.api.gdevflow_api.model.SprintStatus;

public record SprintProgressResponse(
        Long id,
        String name,
        SprintStatus status,
        int progressPercentage,
        long totalTasks,
        long doneTasks) {
}
