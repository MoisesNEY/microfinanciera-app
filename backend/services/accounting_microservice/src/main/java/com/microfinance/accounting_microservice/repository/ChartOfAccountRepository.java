package com.microfinance.accounting_microservice.repository;

import com.microfinance.accounting_microservice.domain.ChartOfAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChartOfAccountRepository extends JpaRepository<ChartOfAccount, UUID> {
    List<ChartOfAccount> findAllByDeleted(Boolean deleted);
    Optional<ChartOfAccount> findByIdAndDeleted(UUID id, Boolean deleted);
    
    /**
     * Busca una cuenta por código, excluyendo las eliminadas.
     */
    Optional<ChartOfAccount> findByAccountCodeAndDeleted(String accountCode, Boolean deleted);
    
    /**
     * Busca una cuenta por nombre (búsqueda case-insensitive), excluyendo las eliminadas.
     */
    Optional<ChartOfAccount> findByAccountNameIgnoreCaseAndDeleted(String accountName, Boolean deleted);
    
    /**
     * Busca una cuenta por nombre lógico (búsqueda case-insensitive), excluyendo las eliminadas.
     */
    Optional<ChartOfAccount> findByLogicalNameIgnoreCaseAndDeleted(String logicalName, Boolean deleted);
}
