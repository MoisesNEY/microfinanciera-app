package com.microfinance.loan_microservice.repository;

import com.microfinance.loan_microservice.domain.LoanApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoanApplicationRepository extends JpaRepository<LoanApplication, UUID> {
    List<LoanApplication> findAllByDeleted(Boolean deleted);
    Optional<LoanApplication> findByIdAndDeleted(UUID id, Boolean deleted);
}
