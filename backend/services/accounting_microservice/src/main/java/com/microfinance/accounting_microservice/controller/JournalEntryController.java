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

@RestController
@RequestMapping("/api/journal-entries")
@RequiredArgsConstructor
public class JournalEntryController {

private final JournalEntryService journalEntryService;

    @GetMapping
    public ResponseEntity<List<JournalEntryDTO>> getAll(
            @RequestParam(value = "deleted", defaultValue = "false") boolean deleted) {
        return ResponseEntity.ok(journalEntryService.findAll(deleted));
    }

    // Endpoint específico para eliminados (para consistencia)
    @GetMapping("/deleted")
    public ResponseEntity<List<JournalEntryDTO>> getDeleted() {
        return ResponseEntity.ok(journalEntryService.findAll(true));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JournalEntryDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(journalEntryService.findById(id));
    }

    @PostMapping
    public ResponseEntity<JournalEntryDTO> create(@Valid @RequestBody JournalEntryDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(journalEntryService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<JournalEntryDTO> update(
            @PathVariable UUID id, 
            @Valid @RequestBody JournalEntryDTO dto) {
        return ResponseEntity.ok(journalEntryService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        journalEntryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}")
    public ResponseEntity<Void> activate(@PathVariable UUID id) {
        journalEntryService.activate(id);
        return ResponseEntity.ok().build();
    }
}