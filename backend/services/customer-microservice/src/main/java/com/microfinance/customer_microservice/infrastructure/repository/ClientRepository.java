package com.microfinance.customer_microservice.infrastructure.repository;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.microfinance.customer_microservice.domain.entity.ClientEntity;

public interface ClientRepository extends JpaRepository<ClientEntity, UUID> {
    List<ClientEntity> findByIsActive(boolean isActive);
    Optional<ClientEntity> findByIdAndIsActive(UUID id, boolean isActive);
}
