package br.com.gdevflow.api.gdevflow_api.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateProjectRequest(
        @NotBlank String name,
        String description,
        Long clientId) {
}
