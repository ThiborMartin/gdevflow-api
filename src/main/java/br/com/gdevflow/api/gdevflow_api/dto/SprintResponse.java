package br.com.gdevflow.api.gdevflow_api.dto;

import java.time.LocalDate;

import br.com.gdevflow.api.gdevflow_api.model.Sprint;
import br.com.gdevflow.api.gdevflow_api.model.SprintStatus;

public record SprintResponse(
        Long id,
        String name,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        SprintStatus status,
        Long projectId,
        String projectName) {

    public static SprintResponse fromEntity(Sprint sprint) {
        return new SprintResponse(
                sprint.getId(),
                sprint.getName(),
                sprint.getDescription(),
                sprint.getStartDate(),
                sprint.getEndDate(),
                sprint.getStatus(),
                sprint.getProject().getId(),
                sprint.getProject().getName());
    }
}
