package com.microfinance.loan_microservice.repository;

import com.microfinance.loan_microservice.domain.LoanProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoanProductRepository extends JpaRepository<LoanProduct, UUID> {
    List<LoanProduct> findAllByDeleted(Boolean deleted);
    Optional<LoanProduct> findByIdAndDeleted(UUID id,Boolean deleted);
}
