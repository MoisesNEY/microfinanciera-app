package com.microfinance.workers_microservice.repository;

import com.microfinance.workers_microservice.domain.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WorkerRepository extends JpaRepository<Worker, UUID> {

    Optional<Worker> findByEmail(String email);

    Optional<Worker> findByUsername(String username);

    Optional<Worker> findByDocumentTypeAndDocumentNumber(
            DocumentType type,
            String number
    );

    Page<Worker> findByStatus(WorkerStatus status, Pageable pageable);

    Optional<Worker> findByIdAndStatus(UUID id, WorkerStatus status);

    Optional<Worker> findByKeycloakId(String keycloakId);

    boolean existsByKeycloakId(String keycloakId);
}