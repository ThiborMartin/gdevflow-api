package br.com.gdevflow.api.gdevflow_api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.gdevflow.api.gdevflow_api.dto.CreateProjectRequest;
import br.com.gdevflow.api.gdevflow_api.dto.ProjectProgressResponse;
import br.com.gdevflow.api.gdevflow_api.dto.ProjectResponse;
import br.com.gdevflow.api.gdevflow_api.dto.UpdateProjectRequest;
import br.com.gdevflow.api.gdevflow_api.service.ProjectService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponse createProject(@RequestBody @Valid CreateProjectRequest request) {
        return projectService.createProject(request);
    }

    @GetMapping
    public List<ProjectResponse> listProjects() {
        return projectService.listProjects();
    }

    @GetMapping("/{id}")
    public ProjectResponse getProject(@PathVariable Long id) {
        return projectService.getProject(id);
    }

    @GetMapping("/{id}/progress")
    public ProjectProgressResponse getProjectProgress(@PathVariable Long id) {
        return projectService.getProjectProgress(id);
    }

    @PutMapping("/{id}")
    public ProjectResponse updateProject(@PathVariable Long id, @RequestBody @Valid UpdateProjectRequest request) {
        return projectService.updateProject(id, request);
    }

    @PatchMapping("/{id}/close")
    public ProjectResponse closeProject(@PathVariable Long id) {
        return projectService.closeProject(id);
    }
}
