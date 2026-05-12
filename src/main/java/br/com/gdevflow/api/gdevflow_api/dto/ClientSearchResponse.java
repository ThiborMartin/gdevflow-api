package br.com.gdevflow.api.gdevflow_api.dto;

import br.com.gdevflow.api.gdevflow_api.model.User;
import br.com.gdevflow.api.gdevflow_api.model.UserRole;

public record ClientSearchResponse(
        Long id,
        String name,
        String email,
        UserRole role) {

    public static ClientSearchResponse fromEntity(User user) {
        return new ClientSearchResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole());
    }
}
