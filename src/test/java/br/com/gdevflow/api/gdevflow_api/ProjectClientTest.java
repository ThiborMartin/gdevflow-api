package br.com.gdevflow.api.gdevflow_api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;

import br.com.gdevflow.api.gdevflow_api.model.Project;
import br.com.gdevflow.api.gdevflow_api.model.ProjectStatus;
import br.com.gdevflow.api.gdevflow_api.model.User;
import br.com.gdevflow.api.gdevflow_api.model.UserRole;
import br.com.gdevflow.api.gdevflow_api.support.ApiIntegrationTestSupport;

class ProjectClientTest extends ApiIntegrationTestSupport {

    @Test
    void freelancerShouldCreateProject() throws Exception {
        User freelancer = persistUser(
                "Freelancer Projeto",
                "freelancer.project@gdevflow.com",
                "senha123",
                UserRole.FREELANCER);
        String token = loginAndGetToken(freelancer.getEmail(), "senha123");

        JsonNode response = createProject(token, "Projeto Principal", "Fluxo principal do sistema");
        Long projectId = response.path("id").asLong();

        Project project = requireProject(projectId);

        assertThat(response.path("status").asText()).isEqualTo(ProjectStatus.IN_PROGRESS.name());
        assertThat(project.getStatus()).isEqualTo(ProjectStatus.IN_PROGRESS);
        assertThat(project.getOwner()).isNotNull();
        assertThat(project.getOwner().getId()).isEqualTo(freelancer.getId());
    }

    @Test
    void freelancerShouldAssignClientToProject() throws Exception {
        User freelancer = persistUser(
                "Freelancer Vinculo",
                "freelancer.client@gdevflow.com",
                "senha123",
                UserRole.FREELANCER);
        User client = persistUser(
                "Cliente Vinculo",
                "client.assign@gdevflow.com",
                "senha123",
                UserRole.CLIENT);
        String freelancerToken = loginAndGetToken(freelancer.getEmail(), "senha123");

        JsonNode projectResponse = createProject(freelancerToken, "Projeto com cliente", "Projeto para validar vinculo");
        Long projectId = projectResponse.path("id").asLong();

        JsonNode assignmentResponse = assignClientToProject(freelancerToken, projectId, client.getId());
        Project project = requireProject(projectId);

        assertThat(assignmentResponse.path("client").path("id").asLong()).isEqualTo(client.getId());
        assertThat(project.getClient()).isNotNull();
        assertThat(project.getClient().getId()).isEqualTo(client.getId());
    }

    @Test
    void clientShouldListOnlyLinkedProjects() throws Exception {
        User freelancer = persistUser(
                "Freelancer List",
                "freelancer.list@gdevflow.com",
                "senha123",
                UserRole.FREELANCER);
        User clientA = persistUser("Cliente A", "client.a@gdevflow.com", "senha123", UserRole.CLIENT);
        User clientB = persistUser("Cliente B", "client.b@gdevflow.com", "senha123", UserRole.CLIENT);

        String freelancerToken = loginAndGetToken(freelancer.getEmail(), "senha123");
        String clientAToken = loginAndGetToken(clientA.getEmail(), "senha123");

        Long firstProjectId = createProject(freelancerToken, "Projeto Cliente A", "Projeto do primeiro cliente")
                .path("id")
                .asLong();
        Long secondProjectId = createProject(freelancerToken, "Projeto Cliente B", "Projeto do segundo cliente")
                .path("id")
                .asLong();

        assignClientToProject(freelancerToken, firstProjectId, clientA.getId());
        assignClientToProject(freelancerToken, secondProjectId, clientB.getId());

        JsonNode response = getClientProjects(clientAToken);

        assertThat(response.isArray()).isTrue();
        assertThat(response).hasSize(1);
        assertThat(response.get(0).path("id").asLong()).isEqualTo(firstProjectId);
        assertThat(response.get(0).path("id").asLong()).isNotEqualTo(secondProjectId);
    }
}
