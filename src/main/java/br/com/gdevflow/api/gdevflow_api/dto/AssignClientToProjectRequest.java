package br.com.gdevflow.api.gdevflow_api.dto;

import jakarta.validation.constraints.NotNull;

public record AssignClientToProjectRequest(
        @NotNull(message = "Informe o clientId do cliente que sera vinculado")
        Long clientId) {
}
