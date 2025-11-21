package com.microfinance.loan_microservice.repository;

import com.microfinance.loan_microservice.domain.LoanSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LoanScheduleRepository extends JpaRepository<LoanSchedule, UUID> {
    List<LoanSchedule> findByLoanIdOrderByInstallmentNo(UUID loanId); // Nuevo: consulta de cuotas por préstamo
    void deleteByLoanId(UUID loanId); // Nuevo: borrar cronograma previo al regenerar
}
