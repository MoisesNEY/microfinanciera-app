package com.microfinance.loan_microservice.web;

import com.microfinance.loan_microservice.domain.LoanSchedule;
import com.microfinance.loan_microservice.dto.LoanScheduleDTOs;
import com.microfinance.loan_microservice.service.LoanScheduleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/loan-schedules")
public class LoanScheduleController {
    private final LoanScheduleService service;

    public LoanScheduleController(LoanScheduleService service) {
        this.service = service;
    }

    @PreAuthorize("hasAnyRole('admin_general', 'asesor_credito', 'cajero', 'cobrador', 'jefe_creditos', 'gerente_general', 'jefe_servicio', 'soporte_ti', 'archivador')")
    @GetMapping
    public List<LoanSchedule> all(@RequestParam(value = "loanId", required = false) UUID loanId) {
        if (loanId != null) {
            return service.byLoan(loanId); // Nuevo: filtro de cuotas por préstamo
        }
        return service.all(false);
    }

    @PreAuthorize("hasAnyRole('admin_general', 'asesor_credito', 'cajero', 'cobrador', 'jefe_creditos', 'gerente_general', 'jefe_servicio', 'soporte_ti', 'archivador')")
    @GetMapping("/{id}")
    public LoanSchedule one(@PathVariable UUID id) {
        return service.one(id, false);
    }

    @PreAuthorize("hasAnyRole('admin_general', 'jefe_creditos', 'gerente_general')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LoanSchedule create(@Valid @RequestBody LoanScheduleDTOs.Create dto) {
        return service.create(dto);
    }

    @PreAuthorize("hasAnyRole('admin_general', 'jefe_creditos', 'gerente_general')")
    @PutMapping("/{id}")
    public LoanSchedule update(@PathVariable UUID id, @Valid @RequestBody LoanScheduleDTOs.Create dto) {
        return service.update(id, dto);
    }

    @PreAuthorize("hasAnyRole('admin_general', 'jefe_creditos', 'gerente_general')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }

    @PreAuthorize("hasAnyRole('admin_general', 'jefe_creditos', 'gerente_general')")
    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void activate(@PathVariable UUID id) {
        service.Activate(id);
    }
}
