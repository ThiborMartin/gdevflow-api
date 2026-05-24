package br.com.gdevflow.api.gdevflow_api.support;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.gdevflow.api.gdevflow_api.model.Project;
import br.com.gdevflow.api.gdevflow_api.model.ProjectStatus;
import br.com.gdevflow.api.gdevflow_api.model.SprintStatus;
import br.com.gdevflow.api.gdevflow_api.model.TaskStatus;
import br.com.gdevflow.api.gdevflow_api.model.User;
import br.com.gdevflow.api.gdevflow_api.model.UserRole;
import br.com.gdevflow.api.gdevflow_api.repository.ProjectMessageRepository;
import br.com.gdevflow.api.gdevflow_api.repository.ProjectRepository;
import br.com.gdevflow.api.gdevflow_api.repository.SprintRepository;
import br.com.gdevflow.api.gdevflow_api.repository.TaskRepository;
import br.com.gdevflow.api.gdevflow_api.repository.UserRepository;
import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public abstract class ApiIntegrationTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected ProjectRepository projectRepository;

    @Autowired
    protected SprintRepository sprintRepository;

    @Autowired
    protected TaskRepository taskRepository;

    @Autowired
    protected ProjectMessageRepository projectMessageRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    protected User persistUser(String name, String email, String rawPassword, UserRole role) {
        User user = new User(
                name,
                email.trim().toLowerCase(),
                passwordEncoder.encode(rawPassword),
                role);

        return userRepository.save(user);
    }

    protected void registerUser(String name, String email, String rawPassword, UserRole role) throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "name", name,
                                "email", email,
                                "password", rawPassword,
                                "role", role.name()))))
                .andExpect(status().isCreated());
    }

    protected JsonNode login(String email, String rawPassword) throws Exception {
        return readJson(mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "email", email,
                                "password", rawPassword))))
                .andExpect(status().isOk()));
    }

    protected String loginAndGetToken(String email, String rawPassword) throws Exception {
        return login(email, rawPassword).path("token").asText();
    }

    protected JsonNode createProject(String token, String name, String description) throws Exception {
        return readJson(mockMvc.perform(post("/projects")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "name", name,
                                "description", description))))
                .andExpect(status().isCreated()));
    }

    protected JsonNode assignClientToProject(String token, Long projectId, Long clientId) throws Exception {
        return readJson(mockMvc.perform(patch("/projects/{id}/client", projectId)
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("clientId", clientId))))
                .andExpect(status().isOk()));
    }

    protected JsonNode createSprint(
            String token,
            Long projectId,
            String name,
            String description,
            LocalDate startDate,
            LocalDate endDate,
            SprintStatus statusValue) throws Exception {
        return readJson(mockMvc.perform(post("/projects/{projectId}/sprints", projectId)
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "name", name,
                                "description", description,
                                "startDate", startDate,
                                "endDate", endDate,
                                "status", statusValue.name()))))
                .andExpect(status().isCreated()));
    }

    protected JsonNode createTask(
            String token,
            Long projectId,
            Long sprintId,
            String title,
            String description,
            LocalDate dueDate,
            TaskStatus statusValue,
            List<Long> dependencyTaskIds) throws Exception {
        return readJson(mockMvc.perform(post("/projects/{projectId}/sprints/{sprintId}/tasks", projectId, sprintId)
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "title", title,
                                "description", description,
                                "dueDate", dueDate,
                                "status", statusValue.name(),
                                "dependencyTaskIds", dependencyTaskIds))))
                .andExpect(status().isCreated()));
    }

    protected JsonNode completeTask(String token, Long taskId) throws Exception {
        return readJson(mockMvc.perform(patch("/tasks/{taskId}/complete", taskId)
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk()));
    }

    protected ResultActions tryCompleteTask(String token, Long taskId) throws Exception {
        return mockMvc.perform(patch("/tasks/{taskId}/complete", taskId)
                .header("Authorization", bearer(token)));
    }

    protected ResultActions requestApproval(String token, Long projectId) throws Exception {
        return mockMvc.perform(patch("/projects/{id}/request-approval", projectId)
                .header("Authorization", bearer(token)));
    }

    protected ResultActions approveProject(String token, Long projectId) throws Exception {
        return mockMvc.perform(patch("/projects/{id}/approve", projectId)
                .header("Authorization", bearer(token)));
    }

    protected JsonNode getProjectProgress(String token, Long projectId) throws Exception {
        return readJson(mockMvc.perform(get("/projects/{id}/progress", projectId)
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk()));
    }

    protected JsonNode getProject(String token, Long projectId) throws Exception {
        return readJson(mockMvc.perform(get("/projects/{id}", projectId)
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk()));
    }

    protected JsonNode getClientProjects(String token) throws Exception {
        return readJson(mockMvc.perform(get("/projects/client")
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk()));
    }

    protected Project requireProject(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalStateException("Projeto nao encontrado no teste"));
    }

    protected LocalDate startDate() {
        return LocalDate.now().plusDays(1);
    }

    protected LocalDate endDate() {
        return LocalDate.now().plusDays(10);
    }

    protected LocalDate dueDate(int daysAhead) {
        return LocalDate.now().plusDays(daysAhead);
    }

    protected String bearer(String token) {
        return "Bearer " + token;
    }

    protected String json(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }

    protected JsonNode readJson(ResultActions actions) throws Exception {
        String content = actions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        return objectMapper.readTree(content);
    }

    protected void assertProjectStatus(Long projectId, ProjectStatus expectedStatus) {
        Project project = requireProject(projectId);
        org.assertj.core.api.Assertions.assertThat(project.getStatus()).isEqualTo(expectedStatus);
    }
}
