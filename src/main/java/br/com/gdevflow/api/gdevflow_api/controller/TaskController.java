package br.com.gdevflow.api.gdevflow_api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.gdevflow.api.gdevflow_api.dto.CreateTaskRequest;
import br.com.gdevflow.api.gdevflow_api.dto.TaskResponse;
import br.com.gdevflow.api.gdevflow_api.dto.UpdateTaskRequest;
import br.com.gdevflow.api.gdevflow_api.dto.UpdateTaskStatusRequest;
import br.com.gdevflow.api.gdevflow_api.service.TaskService;
import jakarta.validation.Valid;

@RestController
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/projects/{projectId}/sprints/{sprintId}/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createTask(
            @PathVariable Long projectId,
            @PathVariable Long sprintId,
            @RequestBody @Valid CreateTaskRequest request) {
        return taskService.createTask(projectId, sprintId, request);
    }

    @GetMapping("/projects/{projectId}/sprints/{sprintId}/tasks")
    public List<TaskResponse> listTasksBySprint(
            @PathVariable Long projectId,
            @PathVariable Long sprintId) {
        return taskService.listTasksBySprint(projectId, sprintId);
    }

    @GetMapping("/tasks/{taskId}")
    public TaskResponse getTask(@PathVariable Long taskId) {
        return taskService.getTask(taskId);
    }

    @PutMapping("/tasks/{taskId}")
    public TaskResponse updateTask(
            @PathVariable Long taskId,
            @RequestBody @Valid UpdateTaskRequest request) {
        return taskService.updateTask(taskId, request);
    }

    @PatchMapping("/tasks/{taskId}/status")
    public TaskResponse updateTaskStatus(
            @PathVariable Long taskId,
            @RequestBody @Valid UpdateTaskStatusRequest request) {
        return taskService.updateTaskStatus(taskId, request);
    }

    @PatchMapping("/tasks/{taskId}/complete")
    public TaskResponse completeTask(@PathVariable Long taskId) {
        return taskService.completeTask(taskId);
    }
}
