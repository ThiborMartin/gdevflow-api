package br.com.gdevflow.api.gdevflow_api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.gdevflow.api.gdevflow_api.dto.CreateProjectRequest;
import br.com.gdevflow.api.gdevflow_api.dto.ProjectResponse;
import br.com.gdevflow.api.gdevflow_api.dto.UpdateProjectRequest;
import br.com.gdevflow.api.gdevflow_api.exception.ForbiddenOperationException;
import br.com.gdevflow.api.gdevflow_api.exception.ResourceNotFoundException;
import br.com.gdevflow.api.gdevflow_api.model.Project;
import br.com.gdevflow.api.gdevflow_api.model.User;
import br.com.gdevflow.api.gdevflow_api.model.UserRole;
import br.com.gdevflow.api.gdevflow_api.repository.ProjectRepository;
import br.com.gdevflow.api.gdevflow_api.repository.UserRepository;
import br.com.gdevflow.api.gdevflow_api.security.AuthenticatedUserService;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final AuthenticatedUserService authenticatedUserService;

    public ProjectService(
            ProjectRepository projectRepository,
            UserRepository userRepository,
            AuthenticatedUserService authenticatedUserService) {
        this.projectRepository = projectRepository;
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
}
