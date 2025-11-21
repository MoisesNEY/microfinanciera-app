package com.microfinance.loan_microservice.service;

import com.microfinance.loan_microservice.domain.Loan;
import com.microfinance.loan_microservice.dto.LoanDTOs;
import com.microfinance.loan_microservice.repository.LoanRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class LoanService {
    private final LoanRepository repo;

    public LoanService(LoanRepository repo) {
        this.repo = repo;
    }

    public List<Loan> all() {
        return repo.findAll();
    }

    public Loan one(UUID id) {
        return repo.findById(id).orElseThrow();
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
        Loan l = one(id);
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
        repo.deleteById(id);
    }

    private void validateMoratoryRate(BigDecimal interestRate, BigDecimal moratoryRate) {
        if (interestRate == null || moratoryRate == null) return;
        BigDecimal maxAllowed = interestRate.multiply(BigDecimal.valueOf(0.25));
        if (moratoryRate.compareTo(maxAllowed) > 0) {
            throw new IllegalArgumentException("La tasa moratoria no puede ser mayor al 25% de la tasa corriente");
        }
    }
}
