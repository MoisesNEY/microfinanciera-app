package com.microfinance.loan_microservice.service;

import com.microfinance.loan_microservice.domain.Loan;
import com.microfinance.loan_microservice.domain.LoanApplication;
import com.microfinance.loan_microservice.dto.LoanApplicationDTOs;
import com.microfinance.loan_microservice.repository.LoanApplicationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class LoanApplicationService {
    private final LoanApplicationRepository repo;

    public LoanApplicationService(LoanApplicationRepository repo) {
        this.repo = repo;
    }

    public List<LoanApplication> all(Boolean deleted) {
        return repo.findAllByDeleted(deleted);
    }

    public LoanApplication one(UUID id, Boolean deleted) {
        return repo.findByIdAndDeleted(id, deleted)
                .orElseThrow(() -> new RuntimeException("LoanApplication not found with id: " + id));
    }

    public LoanApplication create(LoanApplicationDTOs.Create dto) {
        LoanApplication app = new LoanApplication();
        app.setCustomerId(dto.customerId()); // Nuevo: ref a cliente externo
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
        LoanApplication app = one(id, false); // one() ya valida que no esté eliminado
        app.setCustomerId(dto.customerId()); // Nuevo: ref a cliente externo
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
        // Buscar la solicitud activa
        LoanApplication application = repo.findByIdAndDeleted(id, false)
                .orElseThrow(() -> new RuntimeException("LoanApplication not found with id: " + id));
        // Validar que no esté ya eliminada
        if (application.isDeleted()) {
            throw new IllegalStateException("La solicitud de préstamo con id " + id + " ya está inactiva");
        }

        // Marcar como eliminada
        application.setDeleted(true);
        application.setDeletedAt(LocalDateTime.now());

        // Guardar el cambio
        repo.save(application);
    }

    public void Activate(UUID id) {
        // Buscar el aplication eliminado
        LoanApplication application = repo.findByIdAndDeleted(id, true)
                .orElseThrow(() -> new RuntimeException("Loan not found with id: " + id));
        // Validar que esté ya eliminado
        if (application.isDeleted() == false) {
            throw new IllegalStateException("El préstamo con id " + id + " ya está activo");
        }

        // Marcar como eliminado
        application.setDeleted(false);

        // Guardar el cambio
        repo.save(application);
    }
}
