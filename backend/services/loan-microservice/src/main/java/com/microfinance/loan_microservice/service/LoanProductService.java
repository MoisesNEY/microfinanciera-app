package com.microfinance.loan_microservice.service;

import com.microfinance.loan_microservice.domain.Loan;
import com.microfinance.loan_microservice.domain.LoanProduct;
import com.microfinance.loan_microservice.dto.LoanProductDTOs;
import com.microfinance.loan_microservice.repository.LoanProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class LoanProductService {
    private final LoanProductRepository repo;

    public LoanProductService(LoanProductRepository repo) {
        this.repo = repo;
    }

    public List<LoanProduct> all(Boolean deleted) {
        return repo.findAllByDeleted(deleted);
    }

    public LoanProduct one(UUID id, Boolean deleted) {
        return repo.findByIdAndDeleted(id, deleted)
                .orElseThrow(() -> new RuntimeException("LoanProduct not found with id: " + id));
    }

    public LoanProduct create(LoanProductDTOs.Create dto) {
        validateMoratoryRate(dto.interestRate(), dto.moratoryRate()); // Nuevo: validar tope 25% mora
        LoanProduct p = new LoanProduct();
        p.setName(dto.name());
        p.setDescription(dto.description());
        p.setMinAmount(dto.minAmount());
        p.setMaxAmount(dto.maxAmount());
        p.setInterestRate(dto.interestRate());
        p.setTermMonths(dto.termMonths());
        p.setMoratoryRate(dto.moratoryRate());
        p.setPaymentFrequency(dto.paymentFrequency());
        p.setIsActive(dto.isActive());
        return repo.save(p);
    }

    public LoanProduct update(UUID id, LoanProductDTOs.Create dto) {
        validateMoratoryRate(dto.interestRate(), dto.moratoryRate()); // Nuevo: validar tope 25% mora
        LoanProduct p = one(id, false); // one() ya valida que no esté eliminado
        p.setName(dto.name());
        p.setDescription(dto.description());
        p.setMinAmount(dto.minAmount());
        p.setMaxAmount(dto.maxAmount());
        p.setInterestRate(dto.interestRate());
        p.setTermMonths(dto.termMonths());
        p.setMoratoryRate(dto.moratoryRate());
        p.setPaymentFrequency(dto.paymentFrequency());
        p.setIsActive(dto.isActive());
        return repo.save(p);
    }

    public void delete(UUID id) {
        // Buscar el producto activo
        LoanProduct product = repo.findByIdAndDeleted(id, false)
                .orElseThrow(() -> new RuntimeException("LoanProduct not found with id: " + id));

        // Validar que no esté ya eliminado
        if (product.isDeleted()) {
            throw new IllegalStateException("El producto de préstamo con id " + id + " ya está inactivo");
        }

        // Marcar como eliminado
        product.setDeleted(true);
        product.setDeletedAt(LocalDateTime.now());

        // Guardar el cambio
        repo.save(product);
    }

    public void Activate(UUID id) {
        // Buscar el producto eliminado
        LoanProduct product = repo.findByIdAndDeleted(id, true)
                .orElseThrow(() -> new RuntimeException("Loan not found with id: " + id));
        // Validar que esté ya eliminado
        if (product.isDeleted() == false) {
            throw new IllegalStateException("El préstamo con id " + id + " ya está activo");
        }
        // Marcar como eliminado
        product.setDeleted(false);

        // Guardar el cambio
        repo.save(product);
    }

    private void validateMoratoryRate(BigDecimal interestRate, BigDecimal moratoryRate) {
        if (interestRate == null || moratoryRate == null) return;
        BigDecimal maxAllowed = interestRate.multiply(BigDecimal.valueOf(0.25));
        if (moratoryRate.compareTo(maxAllowed) > 0) {
            throw new IllegalArgumentException("La tasa moratoria no puede ser mayor al 25% de la tasa corriente");
        }
    }
}
