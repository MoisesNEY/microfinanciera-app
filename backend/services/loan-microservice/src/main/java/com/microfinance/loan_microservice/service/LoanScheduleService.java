package com.microfinance.loan_microservice.service;

import com.microfinance.loan_microservice.domain.Loan;
import com.microfinance.loan_microservice.domain.LoanProduct;
import com.microfinance.loan_microservice.domain.LoanSchedule;
import com.microfinance.loan_microservice.dto.LoanScheduleDTOs;
import com.microfinance.loan_microservice.repository.LoanScheduleRepository;
import com.microfinance.loan_microservice.service.AmortizationCalculator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class LoanScheduleService {
    private final LoanScheduleRepository repo;

    public LoanScheduleService(LoanScheduleRepository repo) {
        this.repo = repo;
    }

    public List<LoanSchedule> all(Boolean deleted) {
        return repo.findAllByDeleted(deleted);
    }

    public LoanSchedule one(UUID id, Boolean deleted) {
        return repo.findByIdAndDeleted(id, deleted)
                .orElseThrow(() -> new RuntimeException(" LoanSchedule not found with id: " + id));
    }

    @Transactional
    public List<LoanSchedule> generateForLoan(com.microfinance.loan_microservice.domain.Loan loan) {
        repo.deleteByLoanId(loan.getId()); // Nuevo: limpiar cronograma previo
        List<LoanSchedule> schedules = AmortizationCalculator.buildSchedule(loan); // Nuevo: interes sobre saldo
        return repo.saveAll(schedules);
    }
    public List<LoanSchedule> byLoan(UUID loanId) {
        return repo.findByLoanIdOrderByInstallmentNo(loanId); // Nuevo: filtro por prestamo, orden cuotas
    }
    public LoanSchedule one(UUID id) {
        return repo.findById(id).orElseThrow();
    }
    // Método auxiliar para buscar por ID y estado deleted
    public LoanSchedule findByIdAndDeleted(UUID id, boolean deleted) {
        return repo.findByIdAndDeleted(id, deleted)
                .orElseThrow(() -> new RuntimeException("Cronograma no encontrado con id: " + id + " y eliminado: " + deleted));
    }


    public LoanSchedule create(LoanScheduleDTOs.Create dto) {
        LoanSchedule s = new LoanSchedule();
        s.setLoanId(dto.loanId());
        s.setInstallmentNo(dto.installmentNo());
        s.setDueDate(dto.dueDate());
        s.setPrincipalDue(dto.principalDue());
        s.setInterestDue(dto.interestDue());
        s.setTotalDue(dto.totalDue());
        s.setPrincipalPaid(defaultZero(dto.principalPaid()));
        s.setInterestPaid(defaultZero(dto.interestPaid()));
        s.setTotalPaid(defaultZero(dto.totalPaid()));
        s.setStatus(dto.status());
        return repo.save(s);
    }

    public LoanSchedule update(UUID id, LoanScheduleDTOs.Create dto) {
        //  Usar el método auxiliar en lugar de one(id, false)
        LoanSchedule s = findByIdAndDeleted(id, false);
        s.setLoanId(dto.loanId());
        s.setInstallmentNo(dto.installmentNo());
        s.setDueDate(dto.dueDate());
        s.setPrincipalDue(dto.principalDue());
        s.setInterestDue(dto.interestDue());
        s.setTotalDue(dto.totalDue());
        s.setPrincipalPaid(defaultZero(dto.principalPaid()));
        s.setInterestPaid(defaultZero(dto.interestPaid()));
        s.setTotalPaid(defaultZero(dto.totalPaid()));
        s.setStatus(dto.status());
        return repo.save(s);
    }

    public void delete(UUID id) {
        // Buscar el cronograma activo
        LoanSchedule s = repo.findByIdAndDeleted(id, false)
                .orElseThrow(() -> new RuntimeException("Loan not found with id: " + id));
        // Validar que no esté ya eliminado
        if (s.isDeleted()) {
            throw new IllegalStateException("El cronograma con id " + id + " ya está inactivo");
        }
        // Marcar como eliminado
        s.setDeleted(true);
        s.setDeletedAt(LocalDateTime.now());
        // Guardar el cambio
        repo.save(s);
    }
    public void Activate(UUID id) {
        // Buscar el cronograma eliminado
        LoanSchedule s = repo.findByIdAndDeleted(id, true)
                .orElseThrow(() -> new RuntimeException(" not found with id: " + id));
        // Validar que esté ya eliminado
        if (s.isDeleted() == false) {
            throw new IllegalStateException("El cronograma con id " + id + " ya está activo");
        }
        // Marcar como eliminado
        s.setDeleted(false);
        // Guardar el cambio
        repo.save(s);
    }
    private BigDecimal defaultZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
