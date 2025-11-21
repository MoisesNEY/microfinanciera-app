package com.microfinance.loan_microservice.repository;

import com.microfinance.loan_microservice.domain.LoanPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LoanPaymentRepository extends JpaRepository<LoanPayment, UUID> {
    List<LoanPayment> findByLoanIdOrderByPaymentDateAsc(UUID loanId); // Nuevo: pagos por préstamo ordenados
}
