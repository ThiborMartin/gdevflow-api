package br.com.gdevflow.api.gdevflow_api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.gdevflow.api.gdevflow_api.dto.CreateSprintRequest;
import br.com.gdevflow.api.gdevflow_api.dto.SprintResponse;
import br.com.gdevflow.api.gdevflow_api.dto.UpdateSprintRequest;
import br.com.gdevflow.api.gdevflow_api.service.SprintService;
import jakarta.validation.Valid;

@RestController
public class SprintController {

    private final SprintService sprintService;

    public SprintController(SprintService sprintService) {
        this.sprintService = sprintService;
    }

    @PostMapping("/projects/{projectId}/sprints")
    @ResponseStatus(HttpStatus.CREATED)
    public SprintResponse createSprint(
            @PathVariable Long projectId,
            @RequestBody @Valid CreateSprintRequest request) {
        return sprintService.createSprint(projectId, request);
    }

    @GetMapping("/projects/{projectId}/sprints")
    public List<SprintResponse> listProjectSprints(@PathVariable Long projectId) {
        return sprintService.listSprintsByProject(projectId);
    }

    @GetMapping("/sprints/{id}")
    public SprintResponse getSprint(@PathVariable Long id) {
        return sprintService.getSprint(id);
    }

    @PutMapping("/sprints/{id}")
    public SprintResponse updateSprint(@PathVariable Long id, @RequestBody @Valid UpdateSprintRequest request) {
        return sprintService.updateSprint(id, request);
    }
}
