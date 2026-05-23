package br.com.gdevflow.api.gdevflow_api.dto;

import java.time.LocalDateTime;

import br.com.gdevflow.api.gdevflow_api.model.ProjectMessage;
import br.com.gdevflow.api.gdevflow_api.model.UserRole;

public record ProjectMessageResponse(
        Long id,
        String content,
        LocalDateTime createdAt,
        Long projectId,
        Long senderId,
        String senderName,
        UserRole senderRole) {

    public static ProjectMessageResponse fromEntity(ProjectMessage message) {
        return new ProjectMessageResponse(
                message.getId(),
                message.getContent(),
                message.getCreatedAt(),
                message.getProject().getId(),
                message.getSender().getId(),
                message.getSender().getName(),
                message.getSender().getRole());
    }
}
