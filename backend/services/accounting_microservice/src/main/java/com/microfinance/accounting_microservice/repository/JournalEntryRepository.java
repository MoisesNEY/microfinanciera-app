package com.microfinance.accounting_microservice.repository;

import com.microfinance.accounting_microservice.domain.JournalEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface JournalEntryRepository extends JpaRepository<JournalEntry, UUID> {}

