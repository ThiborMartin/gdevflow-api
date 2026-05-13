package br.com.gdevflow.api.gdevflow_api.dto;

import br.com.gdevflow.api.gdevflow_api.model.User;
import br.com.gdevflow.api.gdevflow_api.model.UserRole;

public record UserSummaryResponse(
        Long id,
        String name,
        String email,
        UserRole role) {

    public static UserSummaryResponse fromEntity(User user) {
        if (user == null) {
            return null;
        }

        return new UserSummaryResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole());
    }
}
