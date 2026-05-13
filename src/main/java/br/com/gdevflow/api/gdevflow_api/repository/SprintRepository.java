package br.com.gdevflow.api.gdevflow_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.gdevflow.api.gdevflow_api.model.Sprint;

@Repository
public interface SprintRepository extends JpaRepository<Sprint, Long> {

    List<Sprint> findAllByProjectIdAndProjectOwnerIdOrderByStartDateAsc(Long projectId, Long ownerId);

    Optional<Sprint> findByIdAndProjectOwnerId(Long id, Long ownerId);

    long countByProjectId(Long projectId);

    boolean existsByProjectIdAndNameIgnoreCase(Long projectId, String name);

    boolean existsByProjectIdAndNameIgnoreCaseAndIdNot(Long projectId, String name, Long id);
}
