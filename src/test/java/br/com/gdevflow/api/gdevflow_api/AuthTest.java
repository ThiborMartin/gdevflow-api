package br.com.gdevflow.api.gdevflow_api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;

import br.com.gdevflow.api.gdevflow_api.model.User;
import br.com.gdevflow.api.gdevflow_api.model.UserRole;
import br.com.gdevflow.api.gdevflow_api.support.ApiIntegrationTestSupport;

class AuthTest extends ApiIntegrationTestSupport {

    @Test
    void shouldRegisterFreelancer() throws Exception {
        String email = "freelancer.auth@gdevflow.com";
        String rawPassword = "senha123";

        registerUser("Freelancer Auth", email, rawPassword, UserRole.FREELANCER);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Usuario nao encontrado"));

        assertThat(user.getRole()).isEqualTo(UserRole.FREELANCER);
        assertThat(user.getPassword()).isNotEqualTo(rawPassword);
        assertThat(passwordEncoder.matches(rawPassword, user.getPassword())).isTrue();
    }

    @Test
    void shouldRegisterClient() throws Exception {
        String email = "client.auth@gdevflow.com";

        registerUser("Cliente Auth", email, "senha123", UserRole.CLIENT);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Usuario nao encontrado"));

        assertThat(user.getRole()).isEqualTo(UserRole.CLIENT);
    }

    @Test
    void shouldLoginAndReturnTokenAndRole() throws Exception {
        String email = "login.auth@gdevflow.com";
        String rawPassword = "senha123";

        registerUser("Usuario Login", email, rawPassword, UserRole.FREELANCER);

        JsonNode response = login(email, rawPassword);

        assertThat(response.path("token").asText()).isNotBlank();
        assertThat(response.path("role").asText()).isEqualTo(UserRole.FREELANCER.name());
    }
}
