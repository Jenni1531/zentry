package zentry.back.api.ai.repositories;

import zentry.back.api.ai.models.iaModels;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.Optional;

public interface iaModelsRepository extends JpaRepository<iaModels, UUID> {
    Page<iaModels> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);
    Optional<iaModels> findByNombre(String nombre);
    boolean existsByNombre(String nombre);
}
