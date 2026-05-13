package br.com.gdevflow.api.gdevflow_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.gdevflow.api.gdevflow_api.model.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findAllByOwnerIdOrderByCreatedAtDesc(Long ownerId);

    List<Project> findAllByClientIdOrderByCreatedAtDesc(Long clientId);

    Optional<Project> findByIdAndOwnerId(Long id, Long ownerId);
}
