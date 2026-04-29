package br.com.gdevflow.api.gdevflow_api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.gdevflow.api.gdevflow_api.dto.CreateSprintRequest;
import br.com.gdevflow.api.gdevflow_api.dto.SprintResponse;
import br.com.gdevflow.api.gdevflow_api.dto.UpdateSprintRequest;
import br.com.gdevflow.api.gdevflow_api.exception.ForbiddenOperationException;
import br.com.gdevflow.api.gdevflow_api.exception.ResourceNotFoundException;
import br.com.gdevflow.api.gdevflow_api.model.Project;
import br.com.gdevflow.api.gdevflow_api.model.Sprint;
import br.com.gdevflow.api.gdevflow_api.model.User;
import br.com.gdevflow.api.gdevflow_api.model.UserRole;
import br.com.gdevflow.api.gdevflow_api.repository.ProjectRepository;
import br.com.gdevflow.api.gdevflow_api.repository.SprintRepository;
import br.com.gdevflow.api.gdevflow_api.security.AuthenticatedUserService;

@Service
public class SprintService {

    private final SprintRepository sprintRepository;
    private final ProjectRepository projectRepository;
    private final AuthenticatedUserService authenticatedUserService;

    public SprintService(
            SprintRepository sprintRepository,
            ProjectRepository projectRepository,
            AuthenticatedUserService authenticatedUserService) {
        this.sprintRepository = sprintRepository;
        this.projectRepository = projectRepository;
        this.authenticatedUserService = authenticatedUserService;
    }

    @Transactional
    public SprintResponse createSprint(Long projectId, CreateSprintRequest request) {
        User owner = requireFreelancer();
        Project project = findOwnedProject(projectId, owner.getId());
        validateDates(request.startDate(), request.endDate());
        validateUniqueSprintName(projectId, request.name(), null);

        Sprint sprint = new Sprint(
                request.name(),
                request.description(),
                request.startDate(),
                request.endDate(),
                request.status(),
                project);

        return SprintResponse.fromEntity(sprintRepository.save(sprint));
    }

    @Transactional(readOnly = true)
    public List<SprintResponse> listSprintsByProject(Long projectId) {
        User owner = requireFreelancer();
        findOwnedProject(projectId, owner.getId());

        return sprintRepository.findAllByProjectIdAndProjectOwnerIdOrderByStartDateAsc(projectId, owner.getId())
                .stream()
                .map(SprintResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public SprintResponse getSprint(Long id) {
        User owner = requireFreelancer();
        return SprintResponse.fromEntity(findOwnedSprint(id, owner.getId()));
    }

    @Transactional
    public SprintResponse updateSprint(Long id, UpdateSprintRequest request) {
        User owner = requireFreelancer();
        Sprint sprint = findOwnedSprint(id, owner.getId());
        validateDates(request.startDate(), request.endDate());
        validateUniqueSprintName(sprint.getProject().getId(), request.name(), sprint.getId());

        sprint.setName(request.name());
        sprint.setDescription(request.description());
        sprint.setStartDate(request.startDate());
        sprint.setEndDate(request.endDate());
        sprint.setStatus(request.status());

        return SprintResponse.fromEntity(sprintRepository.save(sprint));
    }

    private User requireFreelancer() {
        User currentUser = authenticatedUserService.getCurrentUser();

        if (currentUser.getRole() != UserRole.FREELANCER) {
            throw new ForbiddenOperationException("Apenas freelancers podem gerenciar sprints");
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

    private void validateDates(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("Data final da sprint nao pode ser anterior a data inicial");
        }
    }

    private void validateUniqueSprintName(Long projectId, String name, Long sprintId) {
        String normalizedName = name == null ? "" : name.trim();

        boolean exists = sprintId == null
                ? sprintRepository.existsByProjectIdAndNameIgnoreCase(projectId, normalizedName)
                : sprintRepository.existsByProjectIdAndNameIgnoreCaseAndIdNot(projectId, normalizedName, sprintId);

        if (exists) {
            throw new IllegalArgumentException("Ja existe uma sprint com esse nome neste projeto");
        }
    }
}
