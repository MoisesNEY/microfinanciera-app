package com.microfinance.customer_microservice.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.microfinance.customer_microservice.domain.entity.ClientEntity;

import com.microfinance.customer_microservice.domain.enums.DocumentType;
public interface ClientRepository extends JpaRepository<ClientEntity, UUID> {

    List<ClientEntity> findByActive(boolean active);

    Optional<ClientEntity> findByIdAndActive(UUID id, boolean active);

    @Query("SELECT c FROM ClientEntity c ORDER BY c.createdAt DESC LIMIT 1")
    Optional<ClientEntity> findTopByOrderByCreatedAtDesc();

    boolean existsByIdDocumentNumberAndIdDocumentType(String idDocumentNumber, DocumentType idDocumentType);

    default Optional<ClientEntity> findActiveById(UUID id) {
        return findByIdAndActive(id, true);
    }
}