package br.com.gdevflow.api.gdevflow_api.dto;

import java.time.LocalDateTime;

import br.com.gdevflow.api.gdevflow_api.model.Project;

public record ProjectResponse(
        Long id,
        String name,
        String description,
        LocalDateTime createdAt,
        boolean closed,
        ProjectUserSummaryResponse owner,
        ProjectUserSummaryResponse client) {

    public static ProjectResponse fromEntity(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getCreatedAt(),
                project.isClosed(),
                ProjectUserSummaryResponse.fromEntity(project.getOwner()),
                ProjectUserSummaryResponse.fromEntity(project.getClient()));
    }
}
