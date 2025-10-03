package com.microfinance.accounting_microservice.service;

import com.microfinance.accounting_microservice.dto.*;
import com.microfinance.accounting_microservice.domain.*;
import com.microfinance.accounting_microservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;

    public TransactionResponseDTO create(TransactionRequestDTO dto) {
        Transaction transaction = Transaction.builder()
                .id(UUID.randomUUID())
                .transactionType(TransactionType.valueOf(dto.getTransactionType()))
                .relatedEntityId(dto.getRelatedEntityId())
                .amount(dto.getAmount())
                .transactionDate(dto.getTransactionDate())
                .description(dto.getDescription())
                .build();

        Transaction saved = transactionRepository.save(transaction);

        return TransactionResponseDTO.builder()
                .id(saved.getId())
                .transactionType(saved.getTransactionType().name())
                .amount(saved.getAmount())
                .transactionDate(saved.getTransactionDate())
                .description(saved.getDescription())
                .build();
    }

    public List<TransactionResponseDTO> findAll() {
        return transactionRepository.findAll().stream()
                .map(t -> TransactionResponseDTO.builder()
                        .id(t.getId())
                        .transactionType(t.getTransactionType().name())
                        .amount(t.getAmount())
                        .transactionDate(t.getTransactionDate())
                        .description(t.getDescription())
                        .build())
                .collect(Collectors.toList());
    }
}

