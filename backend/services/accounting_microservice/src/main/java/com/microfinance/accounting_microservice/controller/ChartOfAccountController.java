package com.microfinance.accounting_microservice.controller;

import com.microfinance.accounting_microservice.dto.ChartOfAccountDTO;
import com.microfinance.accounting_microservice.service.ChartOfAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/chart-of-accounts")
@RequiredArgsConstructor
public class ChartOfAccountController {

    private final ChartOfAccountService chartOfAccountService;

    @PreAuthorize("hasAnyRole('admin_general', 'contador', 'asistente_contable', 'jefe_contabilidad', 'auditor_interno', 'jefe_auditoria', 'gerente_general', 'analista_financiero', 'jefe_finanzas', 'tesorero')")
    @GetMapping
    public ResponseEntity<List<ChartOfAccountDTO>> getAll(
            @RequestParam(value = "deleted", defaultValue = "false") boolean deleted) {
        return ResponseEntity.ok(chartOfAccountService.findAll(deleted));
    }

    // Endpoint específico para eliminados
    @PreAuthorize("hasAnyRole('admin_general', 'contador', 'asistente_contable', 'jefe_contabilidad', 'auditor_interno', 'jefe_auditoria', 'gerente_general', 'analista_financiero', 'jefe_finanzas', 'tesorero')")
    @GetMapping("/deleted")
    public ResponseEntity<List<ChartOfAccountDTO>> getDeleted() {
        return ResponseEntity.ok(chartOfAccountService.findAll(true));
    }

    @PreAuthorize("hasAnyRole('admin_general', 'contador', 'asistente_contable', 'jefe_contabilidad', 'auditor_interno', 'jefe_auditoria', 'gerente_general', 'analista_financiero', 'jefe_finanzas', 'tesorero')")
    @GetMapping("/{id}")
    public ResponseEntity<ChartOfAccountDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(chartOfAccountService.findById(id));
    }

    @PreAuthorize("hasAnyRole('admin_general', 'contador', 'jefe_contabilidad')")
    @PostMapping
    public ResponseEntity<ChartOfAccountDTO> create(@Valid @RequestBody ChartOfAccountDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(chartOfAccountService.create(dto));
    }

    @PreAuthorize("hasAnyRole('admin_general', 'contador', 'jefe_contabilidad')")
    @PutMapping("/{id}")
    public ResponseEntity<ChartOfAccountDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody ChartOfAccountDTO dto) {
        return ResponseEntity.ok(chartOfAccountService.update(id, dto));
    }

    @PreAuthorize("hasAnyRole('admin_general', 'contador', 'jefe_contabilidad')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        chartOfAccountService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('admin_general', 'contador', 'jefe_contabilidad')")
    @PostMapping("/{id}")
    public ResponseEntity<Void> activate(@PathVariable UUID id) {
        chartOfAccountService.activate(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Busca una cuenta por código.
     * 
     * @param code Código de la cuenta
     * @return DTO de la cuenta encontrada
     */
    @PreAuthorize("hasAnyRole('admin_general', 'contador', 'asistente_contable', 'jefe_contabilidad', 'auditor_interno', 'jefe_auditoria', 'gerente_general', 'analista_financiero', 'jefe_finanzas', 'tesorero')")
    @GetMapping("/by-code/{code}")
    public ResponseEntity<ChartOfAccountDTO> getByCode(@PathVariable String code) {
        return ResponseEntity.ok(chartOfAccountService.findByCode(code));
    }

    /**
     * Busca una cuenta por nombre (case-insensitive).
     * 
     * @param name Nombre de la cuenta
     * @return DTO de la cuenta encontrada
     */
    @PreAuthorize("hasAnyRole('admin_general', 'contador', 'asistente_contable', 'jefe_contabilidad', 'auditor_interno', 'jefe_auditoria', 'gerente_general', 'analista_financiero', 'jefe_finanzas', 'tesorero')")
    @GetMapping("/by-name/{name}")
    public ResponseEntity<ChartOfAccountDTO> getByName(@PathVariable String name) {
        return ResponseEntity.ok(chartOfAccountService.findByName(name));
    }

    /**
     * Busca una cuenta por nombre lógico (case-insensitive).
     * 
     * @param logicalName Nombre lógico de la cuenta (ej: "CASH_ACCOUNT")
     * @return DTO de la cuenta encontrada
     */
    @PreAuthorize("hasAnyRole('admin_general', 'contador', 'asistente_contable', 'jefe_contabilidad', 'auditor_interno', 'jefe_auditoria', 'gerente_general', 'analista_financiero', 'jefe_finanzas', 'tesorero')")
    @GetMapping("/by-logical-name/{logicalName}")
    public ResponseEntity<ChartOfAccountDTO> getByLogicalName(@PathVariable String logicalName) {
        return ResponseEntity.ok(chartOfAccountService.findByLogicalName(logicalName));
    }

    /**
     * Busca una cuenta por nombre lógico, código o nombre.
     * Orden de búsqueda: 1) Nombre lógico, 2) Código, 3) Nombre.
     * 
     * @param logicalNameOrCodeOrName Nombre lógico, código o nombre de la cuenta
     * @return DTO de la cuenta encontrada
     */
    @PreAuthorize("hasAnyRole('admin_general', 'contador', 'asistente_contable', 'jefe_contabilidad', 'auditor_interno', 'jefe_auditoria', 'gerente_general', 'analista_financiero', 'jefe_finanzas', 'tesorero')")
    @GetMapping("/lookup/{logicalNameOrCodeOrName}")
    public ResponseEntity<ChartOfAccountDTO> lookup(@PathVariable String logicalNameOrCodeOrName) {
        return ResponseEntity.ok(chartOfAccountService.findByCodeOrName(logicalNameOrCodeOrName));
    }
}