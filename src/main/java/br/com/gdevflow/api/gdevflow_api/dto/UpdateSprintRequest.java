package br.com.gdevflow.api.gdevflow_api.dto;

import java.time.LocalDate;

import br.com.gdevflow.api.gdevflow_api.model.SprintStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateSprintRequest(
        @NotBlank String name,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate,
        @NotNull SprintStatus status) {
}
