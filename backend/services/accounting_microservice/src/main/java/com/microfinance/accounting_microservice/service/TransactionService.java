package com.microfinance.accounting_microservice.service;

import com.microfinance.accounting_microservice.dto.TransactionRequestDTO;
import com.microfinance.accounting_microservice.dto.TransactionResponseDTO;
import com.microfinance.accounting_microservice.domain.Transaction;
import com.microfinance.accounting_microservice.domain.TransactionType;
import com.microfinance.accounting_microservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
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
                .deleted(false)
                .build();

        Transaction saved = transactionRepository.save(transaction);
        return toDTO(saved);
    }

    public List<TransactionResponseDTO> findAll(Boolean deleted) {
        return transactionRepository.findAllByDeleted(deleted).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public TransactionResponseDTO findById(UUID id) {
        Transaction transaction = transactionRepository.findByIdAndDeleted(id, false)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));
        return toDTO(transaction);
    }

    public TransactionResponseDTO update(UUID id, TransactionRequestDTO dto) {
        Transaction transaction = transactionRepository.findByIdAndDeleted(id, false)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));

        transaction.setTransactionType(TransactionType.valueOf(dto.getTransactionType()));
        transaction.setRelatedEntityId(dto.getRelatedEntityId());
        transaction.setAmount(dto.getAmount());
        transaction.setTransactionDate(dto.getTransactionDate());
        transaction.setDescription(dto.getDescription());

        Transaction updated = transactionRepository.save(transaction);
        return toDTO(updated);
    }

    public void delete(UUID id) {
        Transaction transaction = transactionRepository.findByIdAndDeleted(id, false)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));
        // Validar que no esté ya eliminado
        if (transaction.isDeleted()) {
            throw new IllegalStateException("La transaccion con id " + id + " ya está inactivo");
        }

        transaction.setDeleted(true);

        transactionRepository.save(transaction);
    }

    public void activate(UUID id) {
        Transaction transaction = transactionRepository.findByIdAndDeleted(id, true)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));

        if (!transaction.isDeleted()) {
            throw new IllegalStateException("La transacción con id " + id + " ya está activa");
        }

        transaction.setDeleted(false);
        transactionRepository.save(transaction);
    }

    private TransactionResponseDTO toDTO(Transaction transaction) {
        return TransactionResponseDTO.builder()
                .id(transaction.getId())
                .transactionType(transaction.getTransactionType().name())
                // .relatedEntityId(transaction.getRelatedEntityId()) ← QUITAR ESTA LÍNEA
                .amount(transaction.getAmount())
                .transactionDate(transaction.getTransactionDate())
                .description(transaction.getDescription())
                .build();
    }
}
