package br.com.gdevflow.api.gdevflow_api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;

import br.com.gdevflow.api.gdevflow_api.model.ProjectStatus;
import br.com.gdevflow.api.gdevflow_api.model.SprintStatus;
import br.com.gdevflow.api.gdevflow_api.model.TaskStatus;
import br.com.gdevflow.api.gdevflow_api.model.User;
import br.com.gdevflow.api.gdevflow_api.model.UserRole;
import br.com.gdevflow.api.gdevflow_api.support.ApiIntegrationTestSupport;

class TaskProgressTest extends ApiIntegrationTestSupport {

    @Test
    void freelancerShouldCreateTaskWithDueDate() throws Exception {
        User freelancer = persistUser(
                "Freelancer Task",
                "freelancer.task@gdevflow.com",
                "senha123",
                UserRole.FREELANCER);
        String token = loginAndGetToken(freelancer.getEmail(), "senha123");

        Long projectId = createProject(token, "Projeto Task", "Projeto para criar tarefa").path("id").asLong();
        Long sprintId = createSprint(
                token,
                projectId,
                "Sprint 1",
                "Sprint principal",
                startDate(),
                endDate(),
                SprintStatus.IN_PROGRESS)
                .path("id")
                .asLong();

        JsonNode taskResponse = createTask(
                token,
                projectId,
                sprintId,
                "Criar tarefa",
                "Tarefa com data limite",
                dueDate(2),
                TaskStatus.TODO,
                List.of());

        assertThat(taskResponse.path("dueDate").asText()).isEqualTo(dueDate(2).toString());
    }

    @Test
    void shouldBlockTaskCompletionWhenDependencyIsPending() throws Exception {
        User freelancer = persistUser(
                "Freelancer Dependencia",
                "freelancer.dep.pending@gdevflow.com",
                "senha123",
                UserRole.FREELANCER);
        String token = loginAndGetToken(freelancer.getEmail(), "senha123");

        Long projectId = createProject(token, "Projeto Dependencias", "Projeto para validar bloqueio").path("id").asLong();
        Long sprintId = createSprint(
                token,
                projectId,
                "Sprint Dependencias",
                "Sprint com dependencias",
                startDate(),
                endDate(),
                SprintStatus.IN_PROGRESS)
                .path("id")
                .asLong();

        Long taskAId = createTask(
                token,
                projectId,
                sprintId,
                "Tarefa A",
                "Dependencia pendente",
                dueDate(2),
                TaskStatus.TODO,
                List.of())
                .path("id")
                .asLong();

        Long taskBId = createTask(
                token,
                projectId,
                sprintId,
                "Tarefa B",
                "Depende da tarefa A",
                dueDate(3),
                TaskStatus.TODO,
                List.of(taskAId))
                .path("id")
                .asLong();

        JsonNode errorResponse = readJson(tryCompleteTask(token, taskBId)
                .andExpect(status().isBadRequest()));

        assertThat(errorResponse.path("message").asText())
                .contains("Nao e possivel concluir a tarefa");
        assertThat(taskRepository.findById(taskBId).orElseThrow().getStatus()).isEqualTo(TaskStatus.TODO);
    }

    @Test
    void shouldCompleteTaskWhenDependencyIsDone() throws Exception {
        User freelancer = persistUser(
                "Freelancer Done",
                "freelancer.dep.done@gdevflow.com",
                "senha123",
                UserRole.FREELANCER);
        String token = loginAndGetToken(freelancer.getEmail(), "senha123");

        Long projectId = createProject(token, "Projeto Done", "Projeto para concluir tarefas").path("id").asLong();
        Long sprintId = createSprint(
                token,
                projectId,
                "Sprint Done",
                "Sprint para concluir tarefas",
                startDate(),
                endDate(),
                SprintStatus.IN_PROGRESS)
                .path("id")
                .asLong();

        Long taskAId = createTask(
                token,
                projectId,
                sprintId,
                "Tarefa Base",
                "Tarefa base",
                dueDate(2),
                TaskStatus.TODO,
                List.of())
                .path("id")
                .asLong();

        Long taskBId = createTask(
                token,
                projectId,
                sprintId,
                "Tarefa Dependente",
                "Tarefa depende da base",
                dueDate(3),
                TaskStatus.TODO,
                List.of(taskAId))
                .path("id")
                .asLong();

        completeTask(token, taskAId);
        JsonNode taskBResponse = completeTask(token, taskBId);

        assertThat(taskBResponse.path("status").asText()).isEqualTo(TaskStatus.DONE.name());
        assertThat(taskRepository.findById(taskBId).orElseThrow().getStatus()).isEqualTo(TaskStatus.DONE);
    }

    @Test
    void shouldCalculateProjectProgressAsOneHundredPercent() throws Exception {
        User freelancer = persistUser(
                "Freelancer Progress",
                "freelancer.progress@gdevflow.com",
                "senha123",
                UserRole.FREELANCER);
        String token = loginAndGetToken(freelancer.getEmail(), "senha123");

        Long projectId = createProject(token, "Projeto Progress", "Projeto para calculo de progresso").path("id").asLong();
        Long sprintId = createSprint(
                token,
                projectId,
                "Sprint Progress",
                "Sprint com tarefas concluidas",
                startDate(),
                endDate(),
                SprintStatus.IN_PROGRESS)
                .path("id")
                .asLong();

        Long firstTaskId = createTask(
                token,
                projectId,
                sprintId,
                "Primeira tarefa",
                "Primeira tarefa do fluxo",
                dueDate(2),
                TaskStatus.TODO,
                List.of())
                .path("id")
                .asLong();

        Long secondTaskId = createTask(
                token,
                projectId,
                sprintId,
                "Segunda tarefa",
                "Segunda tarefa do fluxo",
                dueDate(3),
                TaskStatus.TODO,
                List.of())
                .path("id")
                .asLong();

        completeTask(token, firstTaskId);
        completeTask(token, secondTaskId);

        JsonNode progressResponse = getProjectProgress(token, projectId);

        assertThat(progressResponse.path("progressPercentage").asInt()).isEqualTo(100);
        assertThat(progressResponse.path("doneTasks").asInt()).isEqualTo(2);
        assertThat(progressResponse.path("projectStatus").asText()).isEqualTo(ProjectStatus.IN_PROGRESS.name());
    }
}
