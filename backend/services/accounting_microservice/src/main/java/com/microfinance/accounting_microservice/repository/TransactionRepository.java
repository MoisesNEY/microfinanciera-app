package com.microfinance.accounting_microservice.repository;

import com.microfinance.accounting_microservice.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {}
