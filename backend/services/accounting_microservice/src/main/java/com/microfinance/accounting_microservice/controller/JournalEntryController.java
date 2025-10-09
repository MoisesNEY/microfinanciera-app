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
    public ResponseEntity<List<JournalEntryDTO>> getAll() {
        return ResponseEntity.ok(journalEntryService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<JournalEntryDTO> getById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(journalEntryService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<JournalEntryDTO> create(@Valid @RequestBody JournalEntryDTO dto) {
        return ResponseEntity.ok(journalEntryService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<JournalEntryDTO> update(
            @PathVariable("id") UUID id, 
            @Valid @RequestBody JournalEntryDTO dto) {
        return ResponseEntity.ok(journalEntryService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {
        journalEntryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}