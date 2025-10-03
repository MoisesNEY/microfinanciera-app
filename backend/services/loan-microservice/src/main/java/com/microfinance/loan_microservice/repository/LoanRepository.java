package com.microfinance.loan_microservice.repository;

import com.microfinance.loan_microservice.domain.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LoanRepository extends JpaRepository<Loan, UUID> {
}
