package com.microfinance.loan_microservice.service;

import com.microfinance.loan_microservice.domain.Loan;
import com.microfinance.loan_microservice.dto.LoanDTOs;
import com.microfinance.loan_microservice.repository.LoanRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class LoanService {
    private final LoanRepository repo;

    public LoanService(LoanRepository repo) {
        this.repo = repo;
    }

    public List<Loan> all(Boolean deleted) {
        return repo.findAllByDeleted(deleted);
    }

    public Loan one(UUID id, Boolean deleted) {
        return repo.findByIdAndDeleted(id, deleted)
                .orElseThrow(() -> new RuntimeException("Loan not found with id: " + id));
    }

    public Loan create(LoanDTOs.Create dto) {
        validateMoratoryRate(dto.interestRate(), dto.moratoryRate()); // Nuevo: validar tope 25% mora
        Loan l = new Loan();
        l.setApplicationId(dto.applicationId());
        l.setCustomerId(dto.customerId());
        l.setLoanCode(dto.loanCode());
        l.setPrincipalAmount(dto.principalAmount());
        l.setInterestRate(dto.interestRate());
        l.setMoratoryRate(dto.moratoryRate());
        l.setTermMonths(dto.termMonths());
        l.setPaymentFrequency(dto.paymentFrequency());
        l.setDisbursementDate(dto.disbursementDate());
        l.setMaturityDate(dto.maturityDate());
        l.setStatus(dto.status());
        l.setSectorEconomico(dto.sectorEconomico());
        return repo.save(l);
    }

    public Loan update(UUID id, LoanDTOs.Create dto) {
        validateMoratoryRate(dto.interestRate(), dto.moratoryRate()); // Nuevo: validar tope 25% mora
        Loan l = one(id, false); // one() ya valida que no esté eliminado
        l.setApplicationId(dto.applicationId());
        l.setCustomerId(dto.customerId());
        l.setLoanCode(dto.loanCode());
        l.setPrincipalAmount(dto.principalAmount());
        l.setInterestRate(dto.interestRate());
        l.setMoratoryRate(dto.moratoryRate());
        l.setTermMonths(dto.termMonths());
        l.setPaymentFrequency(dto.paymentFrequency());
        l.setDisbursementDate(dto.disbursementDate());
        l.setMaturityDate(dto.maturityDate());
        l.setStatus(dto.status());
        l.setSectorEconomico(dto.sectorEconomico());
        return repo.save(l);
    }

    public void delete(UUID id) {
        // Buscar el préstamo activo
        Loan loan = repo.findByIdAndDeleted(id, false)
                .orElseThrow(() -> new RuntimeException("Loan not found with id: " + id));

        // Validar que no esté ya eliminado
        if (loan.isDeleted()) {
            throw new IllegalStateException("El préstamo con id " + id + " ya está inactivo");
        }

        // Marcar como eliminado
        loan.setDeleted(true);
        loan.setDeletedAt(LocalDateTime.now());

        // Guardar el cambio
        repo.save(loan);
    }

    public void Activate(UUID id) {
        // Buscar el préstamo eliminado
        Loan loan = repo.findByIdAndDeleted(id, true)
                .orElseThrow(() -> new RuntimeException("Loan not found with id: " + id));
        // Validar que esté ya eliminado
        if (loan.isDeleted() == false) {
            throw new IllegalStateException("El préstamo con id " + id + " ya está activo");
        }

        // Marcar como eliminado
        loan.setDeleted(false);

        // Guardar el cambio
        repo.save(loan);
    }

    private void validateMoratoryRate(BigDecimal interestRate, BigDecimal moratoryRate) {
        if (interestRate == null || moratoryRate == null) return;
        BigDecimal maxAllowed = interestRate.multiply(BigDecimal.valueOf(0.25));
        if (moratoryRate.compareTo(maxAllowed) > 0) {
            throw new IllegalArgumentException("La tasa moratoria no puede ser mayor al 25% de la tasa corriente");
        }
    }
}
