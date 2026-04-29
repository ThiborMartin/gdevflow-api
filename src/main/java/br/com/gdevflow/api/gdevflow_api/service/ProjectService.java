package br.com.gdevflow.api.gdevflow_api.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.gdevflow.api.gdevflow_api.dto.CreateProjectRequest;
import br.com.gdevflow.api.gdevflow_api.dto.ProjectProgressResponse;
import br.com.gdevflow.api.gdevflow_api.dto.ProjectResponse;
import br.com.gdevflow.api.gdevflow_api.dto.SprintProgressResponse;
import br.com.gdevflow.api.gdevflow_api.dto.UpdateProjectRequest;
import br.com.gdevflow.api.gdevflow_api.exception.ForbiddenOperationException;
import br.com.gdevflow.api.gdevflow_api.exception.ResourceNotFoundException;
import br.com.gdevflow.api.gdevflow_api.model.Project;
import br.com.gdevflow.api.gdevflow_api.model.Sprint;
import br.com.gdevflow.api.gdevflow_api.model.Task;
import br.com.gdevflow.api.gdevflow_api.model.TaskStatus;
import br.com.gdevflow.api.gdevflow_api.model.User;
import br.com.gdevflow.api.gdevflow_api.model.UserRole;
import br.com.gdevflow.api.gdevflow_api.repository.ProjectRepository;
import br.com.gdevflow.api.gdevflow_api.repository.SprintRepository;
import br.com.gdevflow.api.gdevflow_api.repository.TaskRepository;
import br.com.gdevflow.api.gdevflow_api.repository.UserRepository;
import br.com.gdevflow.api.gdevflow_api.security.AuthenticatedUserService;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final SprintRepository sprintRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final AuthenticatedUserService authenticatedUserService;

    public ProjectService(
            ProjectRepository projectRepository,
            SprintRepository sprintRepository,
            TaskRepository taskRepository,
            UserRepository userRepository,
            AuthenticatedUserService authenticatedUserService) {
        this.projectRepository = projectRepository;
        this.sprintRepository = sprintRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.authenticatedUserService = authenticatedUserService;
    }

    @Transactional
    public ProjectResponse createProject(CreateProjectRequest request) {
        User owner = requireFreelancer();
        User client = resolveClient(request.clientId());

        Project project = new Project(request.name(), request.description(), owner, client);
        return ProjectResponse.fromEntity(projectRepository.save(project));
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> listProjects() {
        User owner = requireFreelancer();
        return projectRepository.findAllByOwnerIdOrderByCreatedAtDesc(owner.getId())
                .stream()
                .map(ProjectResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProjectResponse getProject(Long id) {
        User owner = requireFreelancer();
        return ProjectResponse.fromEntity(findOwnedProject(id, owner.getId()));
    }

    @Transactional(readOnly = true)
    public ProjectProgressResponse getProjectProgress(Long id) {
        User currentUser = authenticatedUserService.getCurrentUser();
        Project project = findProjectAccessibleForProgress(id, currentUser);
        List<Sprint> sprints = sprintRepository.findAllByProjectIdAndProjectOwnerIdOrderByStartDateAsc(
                project.getId(),
                project.getOwner().getId());
        List<Task> tasks = taskRepository.findAllByProjectIdOrderByCreatedAtDesc(project.getId());

        long totalTasks = tasks.size();
        long todoTasks = countByStatus(tasks, TaskStatus.TODO);
        long inProgressTasks = countByStatus(tasks, TaskStatus.IN_PROGRESS);
        long doneTasks = countByStatus(tasks, TaskStatus.DONE);
        long blockedTasks = countByStatus(tasks, TaskStatus.BLOCKED);
        int progressPercentage = totalTasks == 0 ? 0 : (int) Math.round((doneTasks * 100.0) / totalTasks);

        Map<Long, List<Task>> tasksBySprintId = tasks.stream()
                .collect(Collectors.groupingBy(task -> task.getSprint().getId()));

        List<SprintProgressResponse> sprintProgress = sprints.stream()
                .map(sprint -> toSprintProgress(sprint, tasksBySprintId))
                .toList();

        return new ProjectProgressResponse(
                project.getId(),
                project.getName(),
                progressPercentage,
                sprints.size(),
                totalTasks,
                todoTasks,
                inProgressTasks,
                doneTasks,
                blockedTasks,
                sprintProgress);
    }

    @Transactional
    public ProjectResponse updateProject(Long id, UpdateProjectRequest request) {
        User owner = requireFreelancer();
        Project project = findOwnedProject(id, owner.getId());

        project.setName(request.name());
        project.setDescription(request.description());
        project.setClient(resolveClient(request.clientId()));

        return ProjectResponse.fromEntity(projectRepository.save(project));
    }

    @Transactional
    public ProjectResponse closeProject(Long id) {
        User owner = requireFreelancer();
        Project project = findOwnedProject(id, owner.getId());

        project.setClosed(true);

        return ProjectResponse.fromEntity(projectRepository.save(project));
    }

    private User requireFreelancer() {
        User currentUser = authenticatedUserService.getCurrentUser();

        if (currentUser.getRole() != UserRole.FREELANCER) {
            throw new ForbiddenOperationException("Apenas freelancers podem gerenciar projetos");
        }

        return currentUser;
    }

    private Project findOwnedProject(Long id, Long ownerId) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Projeto nao encontrado"));

        if (!project.getOwner().getId().equals(ownerId)) {
            throw new ForbiddenOperationException("Projeto nao pertence ao freelancer autenticado");
        }

        return project;
    }

    private Project findProjectAccessibleForProgress(Long projectId, User currentUser) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Projeto nao encontrado"));

        if (currentUser.getRole() == UserRole.FREELANCER) {
            if (!project.getOwner().getId().equals(currentUser.getId())) {
                throw new ForbiddenOperationException("Projeto nao pertence ao freelancer autenticado");
            }

            return project;
        }

        if (currentUser.getRole() == UserRole.CLIENT) {
            if (project.getClient() == null || !project.getClient().getId().equals(currentUser.getId())) {
                throw new ForbiddenOperationException("Projeto nao esta vinculado ao cliente autenticado");
            }

            return project;
        }

        throw new ForbiddenOperationException("Usuario nao possui permissao para visualizar o progresso do projeto");
    }

    private User resolveClient(Long clientId) {
        if (clientId == null) {
            return null;
        }

        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente nao encontrado"));

        if (client.getRole() != UserRole.CLIENT) {
            throw new ForbiddenOperationException("Usuario informado como cliente nao possui role CLIENT");
        }

        return client;
    }

    private long countByStatus(List<Task> tasks, TaskStatus status) {
        return tasks.stream()
                .filter(task -> task.getStatus() == status)
                .count();
    }

    private SprintProgressResponse toSprintProgress(Sprint sprint, Map<Long, List<Task>> tasksBySprintId) {
        List<Task> sprintTasks = tasksBySprintId.getOrDefault(sprint.getId(), List.of());
        long totalTasks = sprintTasks.size();
        long doneTasks = sprintTasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.DONE)
                .count();
        int progressPercentage = totalTasks == 0 ? 0 : (int) Math.round((doneTasks * 100.0) / totalTasks);

        return new SprintProgressResponse(
                sprint.getId(),
                sprint.getName(),
                sprint.getStatus(),
                progressPercentage,
                totalTasks,
                doneTasks);
    }
}
