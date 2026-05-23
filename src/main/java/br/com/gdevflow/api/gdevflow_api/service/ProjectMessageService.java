package br.com.gdevflow.api.gdevflow_api.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.com.gdevflow.api.gdevflow_api.dto.CreateProjectMessageRequest;
import br.com.gdevflow.api.gdevflow_api.dto.ProjectMessageResponse;
import br.com.gdevflow.api.gdevflow_api.exception.ForbiddenOperationException;
import br.com.gdevflow.api.gdevflow_api.exception.ResourceNotFoundException;
import br.com.gdevflow.api.gdevflow_api.model.Project;
import br.com.gdevflow.api.gdevflow_api.model.ProjectMessage;
import br.com.gdevflow.api.gdevflow_api.model.User;
import br.com.gdevflow.api.gdevflow_api.model.UserRole;
import br.com.gdevflow.api.gdevflow_api.repository.ProjectMessageRepository;
import br.com.gdevflow.api.gdevflow_api.repository.ProjectRepository;
import br.com.gdevflow.api.gdevflow_api.security.AuthenticatedUserService;

@Service
public class ProjectMessageService {

    private final ProjectMessageRepository projectMessageRepository;
    private final ProjectRepository projectRepository;
    private final AuthenticatedUserService authenticatedUserService;

    public ProjectMessageService(
            ProjectMessageRepository projectMessageRepository,
            ProjectRepository projectRepository,
            AuthenticatedUserService authenticatedUserService) {
        this.projectMessageRepository = projectMessageRepository;
        this.projectRepository = projectRepository;
        this.authenticatedUserService = authenticatedUserService;
    }

    @Transactional(readOnly = true)
    public List<ProjectMessageResponse> listMessages(Long projectId) {
        User currentUser = authenticatedUserService.getCurrentUser();
        findProjectAccessibleForChat(projectId, currentUser);

        return projectMessageRepository.findAllByProjectIdOrderByCreatedAtAscIdAsc(projectId)
                .stream()
                .map(ProjectMessageResponse::fromEntity)
                .toList();
    }

    @Transactional
    public ProjectMessageResponse sendMessage(Long projectId, CreateProjectMessageRequest request) {
        User currentUser = authenticatedUserService.getCurrentUser();
        Project project = findProjectAccessibleForChat(projectId, currentUser);
        validateChatEnabled(project);

        ProjectMessage message = new ProjectMessage(request.content().trim(), project, currentUser);
        return ProjectMessageResponse.fromEntity(projectMessageRepository.save(message));
    }

    private Project findProjectAccessibleForChat(Long projectId, User currentUser) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Projeto nao encontrado"));

        if (currentUser.getRole() == UserRole.FREELANCER
                && project.getOwner().getId().equals(currentUser.getId())) {
            return project;
        }

        if (currentUser.getRole() == UserRole.CLIENT
                && project.getClient() != null
                && project.getClient().getId().equals(currentUser.getId())) {
            return project;
        }

        throw new ForbiddenOperationException("Usuario nao possui permissao para acessar o chat deste projeto");
    }

    private void validateChatEnabled(Project project) {
        if (project.getClient() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Vincule um cliente ao projeto antes de usar o chat.");
        }
    }
}
