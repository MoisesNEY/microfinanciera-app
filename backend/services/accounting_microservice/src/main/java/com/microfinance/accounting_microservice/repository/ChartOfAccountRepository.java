package com.microfinance.accounting_microservice.repository;

import com.microfinance.accounting_microservice.domain.ChartOfAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChartOfAccountRepository extends JpaRepository<ChartOfAccount, Integer> {}
