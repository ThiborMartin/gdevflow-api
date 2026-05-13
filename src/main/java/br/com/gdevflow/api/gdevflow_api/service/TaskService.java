package br.com.gdevflow.api.gdevflow_api.service;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.gdevflow.api.gdevflow_api.dto.CreateTaskRequest;
import br.com.gdevflow.api.gdevflow_api.dto.TaskResponse;
import br.com.gdevflow.api.gdevflow_api.dto.UpdateTaskRequest;
import br.com.gdevflow.api.gdevflow_api.dto.UpdateTaskStatusRequest;
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
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final SprintRepository sprintRepository;
    private final UserRepository userRepository;
    private final AuthenticatedUserService authenticatedUserService;

    public TaskService(
            TaskRepository taskRepository,
            ProjectRepository projectRepository,
            SprintRepository sprintRepository,
            UserRepository userRepository,
            AuthenticatedUserService authenticatedUserService) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.sprintRepository = sprintRepository;
        this.userRepository = userRepository;
        this.authenticatedUserService = authenticatedUserService;
    }

    @Transactional
    public TaskResponse createTask(Long projectId, Long sprintId, CreateTaskRequest request) {
        User owner = requireFreelancer();
        Project project = findOwnedProject(projectId, owner.getId());
        Sprint sprint = findOwnedSprint(sprintId, owner.getId());
        validateSprintBelongsToProject(sprint, projectId);
        validateTaskDueDate(request.dueDate(), sprint, LocalDate.now());

        Task task = new Task(
                request.title(),
                request.description(),
                request.dueDate(),
                request.status(),
                sprint,
                project,
                resolveAssignedUser(request.assignedToId()));
        task.setDependencies(resolveDependencies(request.dependencyTaskIds(), projectId, null));
        validateTaskCompletionDependencies(task.getStatus(), task.getDependencies());

        return TaskResponse.fromEntity(taskRepository.save(task));
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> listTasksBySprint(Long projectId, Long sprintId) {
        User owner = requireFreelancer();
        findOwnedProject(projectId, owner.getId());
        Sprint sprint = findOwnedSprint(sprintId, owner.getId());
        validateSprintBelongsToProject(sprint, projectId);

        return taskRepository.findAllByProjectIdAndSprintIdOrderByCreatedAtDesc(projectId, sprintId)
                .stream()
                .map(TaskResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public TaskResponse getTask(Long taskId) {
        User owner = requireFreelancer();
        return TaskResponse.fromEntity(findOwnedTask(taskId, owner.getId()));
    }

    @Transactional
    public TaskResponse updateTask(Long taskId, UpdateTaskRequest request) {
        User owner = requireFreelancer();
        Task task = findOwnedTask(taskId, owner.getId());
        validateTaskDueDate(
                request.dueDate(),
                task.getSprint(),
                task.getCreatedAt().toLocalDate());

        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setDueDate(request.dueDate());
        task.setStatus(request.status());
        task.setAssignedTo(resolveAssignedUser(request.assignedToId()));
        task.setDependencies(resolveDependencies(request.dependencyTaskIds(), task.getProject().getId(), task.getId()));
        validateTaskCompletionDependencies(task.getStatus(), task.getDependencies());

        return TaskResponse.fromEntity(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse updateTaskStatus(Long taskId, UpdateTaskStatusRequest request) {
        User owner = requireFreelancer();
        Task task = findOwnedTask(taskId, owner.getId());

        validateTaskCompletionDependencies(request.status(), task.getDependencies());
        task.setStatus(request.status());

        return TaskResponse.fromEntity(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse completeTask(Long taskId) {
        User owner = requireFreelancer();
        Task task = findOwnedTask(taskId, owner.getId());

        validateTaskCompletionDependencies(TaskStatus.DONE, task.getDependencies());
        task.setStatus(TaskStatus.DONE);

        return TaskResponse.fromEntity(taskRepository.save(task));
    }

    private User requireFreelancer() {
        User currentUser = authenticatedUserService.getCurrentUser();

        if (currentUser.getRole() != UserRole.FREELANCER) {
            throw new ForbiddenOperationException("Apenas freelancers podem gerenciar tarefas");
        }

        return currentUser;
    }

    private Project findOwnedProject(Long projectId, Long ownerId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Projeto nao encontrado"));

        if (!project.getOwner().getId().equals(ownerId)) {
            throw new ForbiddenOperationException("Projeto nao pertence ao freelancer autenticado");
        }

        return project;
    }

    private Sprint findOwnedSprint(Long sprintId, Long ownerId) {
        Sprint sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint nao encontrada"));

        if (!sprint.getProject().getOwner().getId().equals(ownerId)) {
            throw new ForbiddenOperationException("Sprint nao pertence ao freelancer autenticado");
        }

        return sprint;
    }

    private Task findOwnedTask(Long taskId, Long ownerId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa nao encontrada"));

        if (!task.getProject().getOwner().getId().equals(ownerId)) {
            throw new ForbiddenOperationException("Tarefa nao pertence ao freelancer autenticado");
        }

        return task;
    }

    private void validateSprintBelongsToProject(Sprint sprint, Long projectId) {
        if (!sprint.getProject().getId().equals(projectId)) {
            throw new IllegalArgumentException("Sprint nao pertence ao projeto informado");
        }
    }

    private User resolveAssignedUser(Long assignedToId) {
        if (assignedToId == null) {
            return null;
        }

        return userRepository.findById(assignedToId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario atribuido nao encontrado"));
    }

    private Set<Task> resolveDependencies(List<Long> dependencyTaskIds, Long projectId, Long taskId) {
        if (dependencyTaskIds == null || dependencyTaskIds.isEmpty()) {
            return new LinkedHashSet<>();
        }

        Set<Task> dependencies = new LinkedHashSet<>();

        for (Long dependencyTaskId : dependencyTaskIds) {
            if (dependencyTaskId == null) {
                continue;
            }

            if (taskId != null && dependencyTaskId.equals(taskId)) {
                throw new IllegalArgumentException("Tarefa nao pode depender dela mesma");
            }

            Task dependencyTask = taskRepository.findById(dependencyTaskId)
                    .orElseThrow(() -> new ResourceNotFoundException("Tarefa dependente nao encontrada"));

            if (!dependencyTask.getProject().getId().equals(projectId)) {
                throw new IllegalArgumentException("Dependencia deve pertencer ao mesmo projeto da tarefa");
            }

            dependencies.add(dependencyTask);
        }

        return dependencies;
    }

    private void validateTaskCompletionDependencies(TaskStatus status, Set<Task> dependencies) {
        if (status != TaskStatus.DONE) {
            return;
        }

        boolean hasPendingDependency = dependencies.stream()
                .anyMatch(dependency -> dependency.getStatus() != TaskStatus.DONE);

        if (hasPendingDependency) {
            throw new IllegalArgumentException(
                    "Nao e possivel concluir a tarefa antes de finalizar todas as dependencias");
        }
    }

    private void validateTaskDueDate(LocalDate dueDate, Sprint sprint, LocalDate minimumDate) {
        if (dueDate.isBefore(minimumDate)) {
            throw new IllegalArgumentException(
                    "Data limite da tarefa nao pode ser anterior a data de criacao");
        }

        if (dueDate.isAfter(sprint.getEndDate())) {
            throw new IllegalArgumentException(
                    "Data limite da tarefa nao pode ser posterior a data final da sprint");
        }
    }
}
