package com.microfinance.customer_microservice.infrastructure.repository;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.microfinance.customer_microservice.domain.entity.ClientEntity;

public interface ClientRepository extends JpaRepository<ClientEntity, UUID> {
    List<ClientEntity> findByActiveTrue(); // Solo clientes activos
    List<ClientEntity> findByActiveFalse(); // Solo clientes inactivos
    Optional<ClientEntity> findByIdAndActiveTrue(UUID id); // Cliente activo por ID
    Optional<ClientEntity> findByIdAndActiveFalse(UUID id); // Cliente inactivo por ID
}