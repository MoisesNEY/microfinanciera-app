package com.microfinance.loan_microservice.service;

import com.microfinance.loan_microservice.domain.LoanApplication;
import com.microfinance.loan_microservice.dto.LoanApplicationDTOs;
import com.microfinance.loan_microservice.repository.LoanApplicationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LoanApplicationService {
    private final LoanApplicationRepository repo;

    public LoanApplicationService(LoanApplicationRepository repo) {
        this.repo = repo;
    }

    public List<LoanApplication> all() {
        return repo.findAll();
    }

    public LoanApplication one(UUID id) {
        return repo.findById(id).orElseThrow();
    }

    public LoanApplication create(LoanApplicationDTOs.Create dto) {
        LoanApplication app = new LoanApplication();
        app.setClientId(dto.clientId());
        app.setLoanProductId(dto.loanProductId());
        app.setRequestedAmount(dto.requestedAmount());
        app.setTermMonths(dto.termMonths());
        app.setStatus(dto.status());
        app.setApplicationDate(dto.applicationDate());
        app.setApprovedDate(dto.approvedDate());
        app.setOfficerId(dto.officerId());
        return repo.save(app);
    }

    public LoanApplication update(UUID id, LoanApplicationDTOs.Create dto) {
        LoanApplication app = one(id);
        app.setClientId(dto.clientId());
        app.setLoanProductId(dto.loanProductId());
        app.setRequestedAmount(dto.requestedAmount());
        app.setTermMonths(dto.termMonths());
        app.setStatus(dto.status());
        app.setApplicationDate(dto.applicationDate());
        app.setApprovedDate(dto.approvedDate());
        app.setOfficerId(dto.officerId());
        return repo.save(app);
    }

    public void delete(UUID id) {
        repo.deleteById(id);
    }
}
