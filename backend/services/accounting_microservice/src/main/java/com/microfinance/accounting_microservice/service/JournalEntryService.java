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
                .build();

        JournalEntry saved = journalEntryRepository.save(entry);

        return JournalEntryDTO.builder()
                .id(saved.getId())
                .transactionId(saved.getTransactionId())
                .accountId(saved.getAccountId())
                .debitAmount(saved.getDebitAmount())
                .creditAmount(saved.getCreditAmount())
                .entryDate(saved.getEntryDate())
                .build();
    }

    public List<JournalEntryDTO> findAll() {
        return journalEntryRepository.findAll().stream()
                .map(j -> JournalEntryDTO.builder()
                        .id(j.getId())
                        .transactionId(j.getTransactionId())
                        .accountId(j.getAccountId())
                        .debitAmount(j.getDebitAmount())
                        .creditAmount(j.getCreditAmount())
                        .entryDate(j.getEntryDate())
                        .build())
                .collect(Collectors.toList());
    }
}

