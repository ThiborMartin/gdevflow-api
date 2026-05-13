package br.com.gdevflow.api.gdevflow_api.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateProjectRequest(
        @NotBlank String name,
        String description) {
}
