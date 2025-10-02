package com.microfinance.loan_microservice.repository;

import com.microfinance.loan_microservice.domain.LoanProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LoanProductRepository extends JpaRepository<LoanProduct, UUID> {
}
