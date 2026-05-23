package br.com.gdevflow.api.gdevflow_api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.gdevflow.api.gdevflow_api.dto.CreateProjectMessageRequest;
import br.com.gdevflow.api.gdevflow_api.dto.ProjectMessageResponse;
import br.com.gdevflow.api.gdevflow_api.service.ProjectMessageService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/projects/{projectId}/messages")
public class ProjectMessageController {

    private final ProjectMessageService projectMessageService;

    public ProjectMessageController(ProjectMessageService projectMessageService) {
        this.projectMessageService = projectMessageService;
    }

    @GetMapping
    public List<ProjectMessageResponse> listMessages(@PathVariable Long projectId) {
        return projectMessageService.listMessages(projectId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectMessageResponse sendMessage(
            @PathVariable Long projectId,
            @RequestBody @Valid CreateProjectMessageRequest request) {
        return projectMessageService.sendMessage(projectId, request);
    }
}
