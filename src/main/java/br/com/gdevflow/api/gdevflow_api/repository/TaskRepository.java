package br.com.gdevflow.api.gdevflow_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.gdevflow.api.gdevflow_api.model.Task;
import br.com.gdevflow.api.gdevflow_api.model.TaskStatus;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByProjectIdAndSprintIdOrderByCreatedAtDesc(Long projectId, Long sprintId);

    List<Task> findAllByProjectIdOrderByCreatedAtDesc(Long projectId);

    long countByProjectId(Long projectId);

    long countByProjectIdAndStatus(Long projectId, TaskStatus status);

    Optional<Task> findByIdAndProjectOwnerId(Long taskId, Long ownerId);
}
