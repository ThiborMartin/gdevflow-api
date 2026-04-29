package br.com.gdevflow.api.gdevflow_api.dto;

import br.com.gdevflow.api.gdevflow_api.model.TaskStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateTaskStatusRequest(@NotNull TaskStatus status) {
}
