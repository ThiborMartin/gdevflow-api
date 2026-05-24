package br.com.gdevflow.api.gdevflow_api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;

import br.com.gdevflow.api.gdevflow_api.model.Project;
import br.com.gdevflow.api.gdevflow_api.model.ProjectStatus;
import br.com.gdevflow.api.gdevflow_api.model.SprintStatus;
import br.com.gdevflow.api.gdevflow_api.model.TaskStatus;
import br.com.gdevflow.api.gdevflow_api.model.User;
import br.com.gdevflow.api.gdevflow_api.model.UserRole;
import br.com.gdevflow.api.gdevflow_api.support.ApiIntegrationTestSupport;

class ProjectApprovalTest extends ApiIntegrationTestSupport {

    @Test
    void shouldBlockApprovalRequestWithPendingTasks() throws Exception {
        ApprovalFixture fixture = createApprovalFixture("pending");

        JsonNode errorResponse = readJson(requestApproval(fixture.freelancerToken, fixture.projectId)
                .andExpect(status().isBadRequest()));

        assertThat(errorResponse.path("message").asText()).contains("Conclua todas as tarefas");
        assertProjectStatus(fixture.projectId, ProjectStatus.IN_PROGRESS);
    }

    @Test
    void freelancerShouldRequestApprovalWhenAllTasksAreDone() throws Exception {
        ApprovalFixture fixture = createApprovalFixture("request");

        completeTask(fixture.freelancerToken, fixture.taskId);

        JsonNode response = readJson(requestApproval(fixture.freelancerToken, fixture.projectId)
                .andExpect(status().isOk()));

        assertThat(response.path("status").asText()).isEqualTo(ProjectStatus.WAITING_CLIENT_APPROVAL.name());
        assertProjectStatus(fixture.projectId, ProjectStatus.WAITING_CLIENT_APPROVAL);
    }

    @Test
    void clientShouldApproveProject() throws Exception {
        ApprovalFixture fixture = createApprovalFixture("approve");

        completeTask(fixture.freelancerToken, fixture.taskId);
        requestApproval(fixture.freelancerToken, fixture.projectId)
                .andExpect(status().isOk());

        JsonNode response = readJson(approveProject(fixture.clientToken, fixture.projectId)
                .andExpect(status().isOk()));

        Project project = requireProject(fixture.projectId);

        assertThat(response.path("status").asText()).isEqualTo(ProjectStatus.COMPLETED.name());
        assertThat(response.path("completedAt").asText()).isNotBlank();
        assertThat(project.getStatus()).isEqualTo(ProjectStatus.COMPLETED);
        assertThat(project.getCompletedAt()).isNotNull();
    }

    @Test
    void nonLinkedClientShouldNotApproveProject() throws Exception {
        ApprovalFixture fixture = createApprovalFixture("forbidden");
        User otherClient = persistUser(
                "Cliente Nao Vinculado",
                "client.other@gdevflow.com",
                "senha123",
                UserRole.CLIENT);
        String otherClientToken = loginAndGetToken(otherClient.getEmail(), "senha123");

        completeTask(fixture.freelancerToken, fixture.taskId);
        requestApproval(fixture.freelancerToken, fixture.projectId)
                .andExpect(status().isOk());

        JsonNode errorResponse = readJson(approveProject(otherClientToken, fixture.projectId)
                .andExpect(status().isForbidden()));

        assertThat(errorResponse.path("status").asInt()).isEqualTo(403);
        assertProjectStatus(fixture.projectId, ProjectStatus.WAITING_CLIENT_APPROVAL);
    }

    private ApprovalFixture createApprovalFixture(String suffix) throws Exception {
        User freelancer = persistUser(
                "Freelancer Approval " + suffix,
                "freelancer.approval." + suffix + "@gdevflow.com",
                "senha123",
                UserRole.FREELANCER);
        User client = persistUser(
                "Cliente Approval " + suffix,
                "client.approval." + suffix + "@gdevflow.com",
                "senha123",
                UserRole.CLIENT);

        String freelancerToken = loginAndGetToken(freelancer.getEmail(), "senha123");
        String clientToken = loginAndGetToken(client.getEmail(), "senha123");

        Long projectId = createProject(
                freelancerToken,
                "Projeto Approval " + suffix,
                "Projeto para fluxo de aprovacao")
                .path("id")
                .asLong();

        assignClientToProject(freelancerToken, projectId, client.getId());

        Long sprintId = createSprint(
                freelancerToken,
                projectId,
                "Sprint Approval " + suffix,
                "Sprint do fluxo de aprovacao",
                startDate(),
                endDate(),
                SprintStatus.IN_PROGRESS)
                .path("id")
                .asLong();

        Long taskId = createTask(
                freelancerToken,
                projectId,
                sprintId,
                "Tarefa Approval " + suffix,
                "Tarefa principal para aprovacao",
                dueDate(2),
                TaskStatus.TODO,
                List.of())
                .path("id")
                .asLong();

        return new ApprovalFixture(projectId, taskId, freelancerToken, clientToken);
    }

    private record ApprovalFixture(
            Long projectId,
            Long taskId,
            String freelancerToken,
            String clientToken) {
    }
}
