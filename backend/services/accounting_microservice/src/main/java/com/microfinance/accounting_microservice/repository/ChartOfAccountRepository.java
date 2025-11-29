package com.microfinance.accounting_microservice.repository;

import com.microfinance.accounting_microservice.domain.ChartOfAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChartOfAccountRepository extends JpaRepository<ChartOfAccount, UUID> {
    List<ChartOfAccount> findAllByDeleted(Boolean deleted);
    Optional<ChartOfAccount> findByIdAndDeleted(UUID id, Boolean deleted);
}
