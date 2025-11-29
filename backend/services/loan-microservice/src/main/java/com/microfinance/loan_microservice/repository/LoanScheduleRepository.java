package com.microfinance.loan_microservice.repository;

import com.microfinance.loan_microservice.domain.LoanSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoanScheduleRepository extends JpaRepository<LoanSchedule, UUID> {
    List<LoanSchedule> findByLoanIdOrderByInstallmentNo(UUID loanId); // Nuevo: consulta de cuotas por préstamo
    Optional<LoanSchedule> findByIdAndDeleted(UUID id, Boolean deleted);
    List<LoanSchedule> findAllByDeleted(Boolean deleted);
    void deleteByLoanId(UUID loanId); // Nuevo: eliminar cronograma por préstamo
    
}

