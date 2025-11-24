package com.microfinance.accounting_microservice.repository;

import com.microfinance.accounting_microservice.domain.ChartOfAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChartOfAccountRepository extends JpaRepository<ChartOfAccount, Integer> {
    List<ChartOfAccount> findAllByDeletedFalse();
    Optional<ChartOfAccount> findByIdAndDeletedFalse(Integer id);
}
