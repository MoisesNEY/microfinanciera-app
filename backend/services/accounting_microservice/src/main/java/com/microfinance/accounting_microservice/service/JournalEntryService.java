package com.microfinance.accounting_microservice.service;

import com.microfinance.accounting_microservice.dto.JournalEntryDTO;
import com.microfinance.accounting_microservice.domain.JournalEntry;
import com.microfinance.accounting_microservice.repository.JournalEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JournalEntryService {

    private final JournalEntryRepository journalEntryRepository;

    public JournalEntryDTO create(JournalEntryDTO dto) {
        JournalEntry entry = JournalEntry.builder()
                .id(UUID.randomUUID())
                .transactionId(dto.getTransactionId())
                .accountId(dto.getAccountId())
                .debitAmount(dto.getDebitAmount())
                .creditAmount(dto.getCreditAmount())
                .entryDate(dto.getEntryDate())
                .deleted(false)
                .build();

        JournalEntry saved = journalEntryRepository.save(entry);
        return toDTO(saved);
    }

    public List<JournalEntryDTO> findAll(Boolean deleted) {
        return journalEntryRepository.findAllByDeleted(deleted).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public JournalEntryDTO findById(UUID id) {
        JournalEntry entry = journalEntryRepository.findByIdAndDeleted(id, false)
                .orElseThrow(() -> new RuntimeException("Journal entry not found with id: " + id));
        return toDTO(entry);
    }

    public JournalEntryDTO update(UUID id, JournalEntryDTO dto) {
        JournalEntry entry = journalEntryRepository.findByIdAndDeleted(id, false)
                .orElseThrow(() -> new RuntimeException("Journal entry not found with id: " + id));
        
        entry.setTransactionId(dto.getTransactionId());
        entry.setAccountId(dto.getAccountId());
        entry.setDebitAmount(dto.getDebitAmount());
        entry.setCreditAmount(dto.getCreditAmount());
        entry.setEntryDate(dto.getEntryDate());

        JournalEntry updated = journalEntryRepository.save(entry);
        return toDTO(updated);
    }

    public void delete(UUID id) {
        JournalEntry entry = journalEntryRepository.findByIdAndDeleted(id, false)
                .orElseThrow(() -> new RuntimeException("Journal entry not found with id: " + id));

        if (entry.isDeleted()) {
            throw new IllegalStateException("El asiento con id " + id + " ya está inactivo");
        }

        entry.setDeleted(true);
        journalEntryRepository.save(entry);
    }

    public void activate(UUID id) {
        JournalEntry entry = journalEntryRepository.findByIdAndDeleted(id, true)
                .orElseThrow(() -> new RuntimeException("Journal entry not found with id: " + id));

        if (!entry.isDeleted()) {
            throw new IllegalStateException("El asiento con id " + id + " ya está activo");
        }

        entry.setDeleted(false);
        journalEntryRepository.save(entry);
    }

    private JournalEntryDTO toDTO(JournalEntry entry) {
        return JournalEntryDTO.builder()
                .id(entry.getId())
                .transactionId(entry.getTransactionId())
                .accountId(entry.getAccountId())
                .debitAmount(entry.getDebitAmount())
                .creditAmount(entry.getCreditAmount())
                .entryDate(entry.getEntryDate())
                .build();
    }
}
