package br.com.gdevflow.api.gdevflow_api.dto;

import java.time.LocalDateTime;

import br.com.gdevflow.api.gdevflow_api.model.Project;

public record ProjectResponse(
        Long id,
        String name,
        String description,
        LocalDateTime createdAt,
        boolean closed,
        Long ownerId,
        String ownerName,
        Long clientId,
        String clientName) {

    public static ProjectResponse fromEntity(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getCreatedAt(),
                project.isClosed(),
                project.getOwner().getId(),
                project.getOwner().getName(),
                project.getClient() != null ? project.getClient().getId() : null,
                project.getClient() != null ? project.getClient().getName() : null);
    }
}
