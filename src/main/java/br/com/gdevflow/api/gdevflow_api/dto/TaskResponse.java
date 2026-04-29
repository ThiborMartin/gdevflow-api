package br.com.gdevflow.api.gdevflow_api.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import br.com.gdevflow.api.gdevflow_api.model.Task;
import br.com.gdevflow.api.gdevflow_api.model.TaskStatus;

public record TaskResponse(
        Long id,
        String title,
        String description,
        TaskStatus status,
        LocalDateTime createdAt,
        LocalDate dueDate,
        Long sprintId,
        String sprintName,
        Long projectId,
        String projectName,
        Long assignedToId,
        String assignedToName,
        List<Long> dependencyTaskIds) {

    public static TaskResponse fromEntity(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getCreatedAt(),
                task.getDueDate(),
                task.getSprint().getId(),
                task.getSprint().getName(),
                task.getProject().getId(),
                task.getProject().getName(),
                task.getAssignedTo() != null ? task.getAssignedTo().getId() : null,
                task.getAssignedTo() != null ? task.getAssignedTo().getName() : null,
                task.getDependencies().stream().map(Task::getId).toList());
    }
}
