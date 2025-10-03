package com.microfinance.loan_microservice.service;

import com.microfinance.loan_microservice.domain.Loan;
import com.microfinance.loan_microservice.dto.LoanDTOs;
import com.microfinance.loan_microservice.repository.LoanRepository;
import org.springframework.stereotype.Service;

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
        Loan l = new Loan();
        l.setApplicationId(dto.applicationId());
        l.setLoanCode(dto.loanCode());
        l.setPrincipalAmount(dto.principalAmount());
        l.setInterestRate(dto.interestRate());
        l.setDisbursementDate(dto.disbursementDate());
        l.setMaturityDate(dto.maturityDate());
        l.setStatus(dto.status());
        l.setSectorEconomico(dto.sectorEconomico());
        return repo.save(l);
    }

    public Loan update(UUID id, LoanDTOs.Create dto) {
        Loan l = one(id);
        l.setApplicationId(dto.applicationId());
        l.setLoanCode(dto.loanCode());
        l.setPrincipalAmount(dto.principalAmount());
        l.setInterestRate(dto.interestRate());
        l.setDisbursementDate(dto.disbursementDate());
        l.setMaturityDate(dto.maturityDate());
        l.setStatus(dto.status());
        l.setSectorEconomico(dto.sectorEconomico());
        return repo.save(l);
    }

    public void delete(UUID id) {
        repo.deleteById(id);
    }
}
