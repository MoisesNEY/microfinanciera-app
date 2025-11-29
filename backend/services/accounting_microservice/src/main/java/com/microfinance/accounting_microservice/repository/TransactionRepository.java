package com.microfinance.accounting_microservice.repository;

import com.microfinance.accounting_microservice.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findAllByDeleted(Boolean deleted);
    Optional<Transaction> findByIdAndDeleted(UUID id, Boolean deleted);
}
