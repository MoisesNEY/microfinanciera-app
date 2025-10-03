package com.microfinance.loan_microservice.service;

import com.microfinance.loan_microservice.domain.LoanProduct;
import com.microfinance.loan_microservice.dto.LoanProductDTOs;
import com.microfinance.loan_microservice.repository.LoanProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LoanProductService {
    private final LoanProductRepository repo;

    public LoanProductService(LoanProductRepository repo) {
        this.repo = repo;
    }

    public List<LoanProduct> all() {
        return repo.findAll();
    }

    public LoanProduct one(UUID id) {
        return repo.findById(id).orElseThrow();
    }

    public LoanProduct create(LoanProductDTOs.Create dto) {
        LoanProduct p = new LoanProduct();
        p.setName(dto.name());
        p.setDescription(dto.description());
        p.setMinAmount(dto.minAmount());
        p.setMaxAmount(dto.maxAmount());
        p.setInterestRate(dto.interestRate());
        p.setTermMonths(dto.termMonths());
        p.setIsActive(dto.isActive());
        return repo.save(p);
    }

    public LoanProduct update(UUID id, LoanProductDTOs.Create dto) {
        LoanProduct p = one(id);
        p.setName(dto.name());
        p.setDescription(dto.description());
        p.setMinAmount(dto.minAmount());
        p.setMaxAmount(dto.maxAmount());
        p.setInterestRate(dto.interestRate());
        p.setTermMonths(dto.termMonths());
        p.setIsActive(dto.isActive());
        return repo.save(p);
    }

    public void delete(UUID id) {
        repo.deleteById(id);
    }
}
