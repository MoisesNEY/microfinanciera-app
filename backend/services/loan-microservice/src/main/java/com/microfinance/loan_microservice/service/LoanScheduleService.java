package com.microfinance.loan_microservice.service;

import com.microfinance.loan_microservice.domain.LoanSchedule;
import com.microfinance.loan_microservice.dto.LoanScheduleDTOs;
import com.microfinance.loan_microservice.repository.LoanScheduleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LoanScheduleService {
    private final LoanScheduleRepository repo;

    public LoanScheduleService(LoanScheduleRepository repo) {
        this.repo = repo;
    }

    public List<LoanSchedule> all() {
        return repo.findAll();
    }

    public LoanSchedule one(UUID id) {
        return repo.findById(id).orElseThrow();
    }

    public LoanSchedule create(LoanScheduleDTOs.Create dto) {
        LoanSchedule s = new LoanSchedule();
        s.setLoanId(dto.loanId());
        s.setDueDate(dto.dueDate());
        s.setPrincipalDue(dto.principalDue());
        s.setInterestDue(dto.interestDue());
        s.setTotalDue(dto.totalDue());
        s.setStatus(dto.status());
        return repo.save(s);
    }

    public LoanSchedule update(UUID id, LoanScheduleDTOs.Create dto) {
        LoanSchedule s = one(id);
        s.setLoanId(dto.loanId());
        s.setDueDate(dto.dueDate());
        s.setPrincipalDue(dto.principalDue());
        s.setInterestDue(dto.interestDue());
        s.setTotalDue(dto.totalDue());
        s.setStatus(dto.status());
        return repo.save(s);
    }

    public void delete(UUID id) {
        repo.deleteById(id);
    }
}
