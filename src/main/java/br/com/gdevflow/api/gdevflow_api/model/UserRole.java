package br.com.gdevflow.api.gdevflow_api.model;

import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public enum UserRole {
    FREELANCER,
    CLIENT;

    public static UserRole fromRegistrationValue(String value) {
        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Informe o tipo de conta. Use CLIENT ou FREELANCER.");
        }

        try {
            return UserRole.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Role invalido. Use CLIENT ou FREELANCER.");
        }
    }
}
