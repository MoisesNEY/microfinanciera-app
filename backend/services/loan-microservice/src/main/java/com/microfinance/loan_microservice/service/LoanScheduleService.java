package com.microfinance.loan_microservice.service;

import com.microfinance.loan_microservice.domain.LoanSchedule;
import com.microfinance.loan_microservice.dto.LoanScheduleDTOs;
import com.microfinance.loan_microservice.repository.LoanScheduleRepository;
import com.microfinance.loan_microservice.service.AmortizationCalculator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
        LoanSchedule s = one(id);
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
        repo.deleteById(id);
    }

    private BigDecimal defaultZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
