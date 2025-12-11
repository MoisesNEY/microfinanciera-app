package com.microfinance.loan_microservice.web;

import com.microfinance.loan_microservice.domain.LoanProduct;
import com.microfinance.loan_microservice.dto.LoanProductDTOs;
import com.microfinance.loan_microservice.service.LoanProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/loan-products")
public class LoanProductController {
    private final LoanProductService service;

    public LoanProductController(LoanProductService service) {
        this.service = service;
    }

    @PreAuthorize("hasAnyRole('admin_general', 'asesor_credito', 'jefe_creditos', 'atencion_cliente', 'gerente_general', 'subgerente', 'analista_riesgo', 'supervisor_creditos', 'jefe_cobranza', 'archivador')")
    @GetMapping
    public List<LoanProduct> all() {
        return service.all(false);
    }

    // Endpoint para obtener SOLO los eliminados
    @PreAuthorize("hasAnyRole('admin_general', 'asesor_credito', 'jefe_creditos', 'atencion_cliente', 'gerente_general', 'jefe_cobranza', 'archivador')")
    @GetMapping("/deleted")
    public List<LoanProduct> deleted() {
        return service.all(true);
    }

    @PreAuthorize("hasAnyRole('admin_general', 'asesor_credito', 'jefe_creditos', 'atencion_cliente', 'gerente_general', 'subgerente', 'analista_riesgo', 'supervisor_creditos', 'jefe_cobranza', 'archivador')")
    @GetMapping("/{id}")
    public LoanProduct one(@PathVariable UUID id) {
        return service.one(id, false);
    }

    @PreAuthorize("hasAnyRole('admin_general', 'jefe_creditos', 'gerente_general', 'jefe_cobranza')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LoanProduct create(@Valid @RequestBody LoanProductDTOs.Create dto) {
        return service.create(dto);
    }

    @PreAuthorize("hasAnyRole('admin_general', 'jefe_creditos', 'gerente_general', 'jefe_cobranza')")
    @PutMapping("/{id}")
    public LoanProduct update(@PathVariable UUID id, @Valid @RequestBody LoanProductDTOs.Create dto) {
        return service.update(id, dto);
    }

    @PreAuthorize("hasAnyRole('admin_general', 'jefe_creditos', 'gerente_general', 'jefe_cobranza')")
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
