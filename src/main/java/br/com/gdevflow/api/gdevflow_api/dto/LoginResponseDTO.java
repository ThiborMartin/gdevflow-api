package br.com.gdevflow.api.gdevflow_api.dto;

import br.com.gdevflow.api.gdevflow_api.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponseDTO {
    private String token;
    private UserRole role;
}
