package br.com.gdevflow.api.gdevflow_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.gdevflow.api.gdevflow_api.model.ProjectMessage;

@Repository
public interface ProjectMessageRepository extends JpaRepository<ProjectMessage, Long> {

    List<ProjectMessage> findAllByProjectIdOrderByCreatedAtAscIdAsc(Long projectId);
}
