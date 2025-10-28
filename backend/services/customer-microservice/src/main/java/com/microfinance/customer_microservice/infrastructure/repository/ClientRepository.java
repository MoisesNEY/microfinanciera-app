package com.microfinance.customer_microservice.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import com.microfinance.customer_microservice.domain.entity.ClientEntity;

public interface ClientRepository extends JpaRepository<ClientEntity, UUID> {

    List<ClientEntity> findByActive(boolean active);

    Optional<ClientEntity> findByIdAndActive(UUID id, boolean active);

    default Optional<ClientEntity> findActiveById(UUID id) {
        return findByIdAndActive(id, true);
    }
}
