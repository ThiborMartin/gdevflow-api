package br.com.gdevflow.api.gdevflow_api.dto;

import java.time.LocalDateTime;

import br.com.gdevflow.api.gdevflow_api.model.Project;
import br.com.gdevflow.api.gdevflow_api.model.ProjectStatus;

public record ProjectResponse(
        Long id,
        String name,
        String description,
        LocalDateTime createdAt,
        ProjectStatus status,
        LocalDateTime completedAt,
        UserSummaryResponse owner,
        UserSummaryResponse client) {

    public static ProjectResponse fromEntity(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getCreatedAt(),
                project.getStatus(),
                project.getCompletedAt(),
                UserSummaryResponse.fromEntity(project.getOwner()),
                UserSummaryResponse.fromEntity(project.getClient()));
    }
}
