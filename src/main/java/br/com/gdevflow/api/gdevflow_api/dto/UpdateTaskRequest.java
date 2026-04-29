package br.com.gdevflow.api.gdevflow_api.dto;

import java.time.LocalDate;
import java.util.List;

import br.com.gdevflow.api.gdevflow_api.model.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateTaskRequest(
        @NotBlank String title,
        String description,
        @NotNull LocalDate dueDate,
        @NotNull TaskStatus status,
        Long assignedToId,
        List<Long> dependencyTaskIds) {
}
