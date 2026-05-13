package br.com.gdevflow.api.gdevflow_api.dto;

import java.time.LocalDate;
import java.util.List;

import br.com.gdevflow.api.gdevflow_api.model.SprintStatus;

public record SprintProgressResponse(
        Long id,
        String name,
        SprintStatus status,
        LocalDate startDate,
        LocalDate endDate,
        int progressPercentage,
        long totalTasks,
        long doneTasks,
        List<TaskProgressResponse> tasks) {
}
