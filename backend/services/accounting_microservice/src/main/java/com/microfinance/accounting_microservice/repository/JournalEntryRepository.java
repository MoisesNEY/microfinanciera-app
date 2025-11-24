package com.microfinance.accounting_microservice.repository;

import com.microfinance.accounting_microservice.domain.JournalEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;
import java.util.Optional;

public interface JournalEntryRepository extends JpaRepository<JournalEntry, UUID> {
    List<JournalEntry> findAllByDeletedFalse();
    Optional<JournalEntry> findByIdAndDeletedFalse(UUID id);
}

