package com.microfinance.workers_microservice.repository;

import com.microfinance.workers_microservice.domain.Worker;
import com.microfinance.workers_microservice.domain.WorkerStatus;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WorkerRepository extends JpaRepository<Worker, UUID> {
  Optional<Worker> findByEmail(String email);
  Optional<Worker> findByDocument(String document);
  Page<Worker> findByStatus(WorkerStatus status, Pageable pageable);
  Page<Worker> findByPositionIgnoreCase(String position, Pageable pageable);
}
