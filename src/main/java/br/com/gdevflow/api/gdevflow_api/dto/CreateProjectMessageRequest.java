package br.com.gdevflow.api.gdevflow_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateProjectMessageRequest(
        @NotBlank
        @Size(max = 2000)
        String content) {
}
