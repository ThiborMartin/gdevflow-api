package br.com.gdevflow.api.gdevflow_api.dto;

import br.com.gdevflow.api.gdevflow_api.model.User;

public record ProjectUserSummaryResponse(
        Long id,
        String name,
        String email) {

    public static ProjectUserSummaryResponse fromEntity(User user) {
        if (user == null) {
            return null;
        }

        return new ProjectUserSummaryResponse(
                user.getId(),
                user.getName(),
                user.getEmail());
    }
}
