package com.microfinance.accounting_microservice.controller;

import com.microfinance.accounting_microservice.dto.JournalEntryDTO;
import com.microfinance.accounting_microservice.service.JournalEntryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/journal-entries")
@RequiredArgsConstructor
public class JournalEntryController {

    private final JournalEntryService journalEntryService;

    @PreAuthorize("hasAnyRole('admin_general', 'contador', 'asistente_contable', 'jefe_contabilidad', 'auditor_interno', 'jefe_auditoria', 'analista_financiero', 'jefe_finanzas', 'tesorero')")
    @GetMapping
    public ResponseEntity<List<JournalEntryDTO>> getAll(
            @RequestParam(value = "deleted", defaultValue = "false") boolean deleted) {
        return ResponseEntity.ok(journalEntryService.findAll(deleted));
    }

    // Endpoint específico para eliminados (para consistencia)
    @PreAuthorize("hasAnyRole('admin_general', 'contador', 'asistente_contable', 'jefe_contabilidad', 'auditor_interno', 'jefe_auditoria', 'analista_financiero', 'jefe_finanzas', 'tesorero')")
    @GetMapping("/deleted")
    public ResponseEntity<List<JournalEntryDTO>> getDeleted() {
        return ResponseEntity.ok(journalEntryService.findAll(true));
    }

    @PreAuthorize("hasAnyRole('admin_general', 'contador', 'asistente_contable', 'jefe_contabilidad', 'auditor_interno', 'jefe_auditoria', 'analista_financiero', 'jefe_finanzas', 'tesorero')")
    @GetMapping("/{id}")
    public ResponseEntity<JournalEntryDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(journalEntryService.findById(id));
    }

    @PreAuthorize("hasAnyRole('admin_general', 'contador', 'jefe_contabilidad')")
    @PostMapping
    public ResponseEntity<JournalEntryDTO> create(@Valid @RequestBody JournalEntryDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(journalEntryService.create(dto));
    }

    @PreAuthorize("hasAnyRole('admin_general', 'contador', 'jefe_contabilidad')")
    @PutMapping("/{id}")
    public ResponseEntity<JournalEntryDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody JournalEntryDTO dto) {
        return ResponseEntity.ok(journalEntryService.update(id, dto));
    }

    @PreAuthorize("hasAnyRole('admin_general', 'contador', 'jefe_contabilidad')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        journalEntryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('admin_general', 'contador', 'jefe_contabilidad')")
    @PostMapping("/{id}")
    public ResponseEntity<Void> activate(@PathVariable UUID id) {
        journalEntryService.activate(id);
        return ResponseEntity.ok().build();
    }
}