package com.microfinance.accounting_microservice.controller;

import com.microfinance.accounting_microservice.dto.JournalEntryDTO;
import com.microfinance.accounting_microservice.service.JournalEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/journal-entries")
@RequiredArgsConstructor
public class JournalEntryController {

    private final JournalEntryService journalEntryService;

    @PostMapping
    public ResponseEntity<JournalEntryDTO> create(@RequestBody JournalEntryDTO dto) {
        return ResponseEntity.ok(journalEntryService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<JournalEntryDTO>> getAll() {
        return ResponseEntity.ok(journalEntryService.findAll());
    }
}
