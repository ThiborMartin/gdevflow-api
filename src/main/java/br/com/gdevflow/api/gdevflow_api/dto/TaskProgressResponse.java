package br.com.gdevflow.api.gdevflow_api.dto;

import java.time.LocalDate;

import br.com.gdevflow.api.gdevflow_api.model.Task;
import br.com.gdevflow.api.gdevflow_api.model.TaskStatus;

public record TaskProgressResponse(
        Long id,
        String title,
        String description,
        TaskStatus status,
        LocalDate dueDate) {

    public static TaskProgressResponse fromEntity(Task task) {
        return new TaskProgressResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getDueDate());
    }
}
