package com.microfinance.accounting_microservice.service;

import com.microfinance.accounting_microservice.domain.AccountType;
import com.microfinance.accounting_microservice.domain.ChartOfAccount;
import com.microfinance.accounting_microservice.dto.AccountBalanceDTO;
import com.microfinance.accounting_microservice.dto.BalanceSheetDTO;
import com.microfinance.accounting_microservice.repository.ChartOfAccountRepository;
import com.microfinance.accounting_microservice.repository.JournalEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ChartOfAccountRepository accountRepository;
    private final JournalEntryRepository journalEntryRepository;

    public BalanceSheetDTO getBalanceSheet(LocalDate date) {
        // End of the day for the given date
        ZonedDateTime zdt = date.atTime(LocalTime.MAX).atZone(ZoneId.of("America/Managua"));

        List<ChartOfAccount> allAccounts = accountRepository.findAll();
        Map<UUID, ChartOfAccount> accountMap = allAccounts.stream()
                .collect(Collectors.toMap(ChartOfAccount::getId, a -> a));

        List<Object[]> balancesData = journalEntryRepository.getBalancesByDate(zdt);

        // Map accountId -> {debit, credit}
        Map<UUID, BigDecimal[]> rawBalances = new HashMap<>();
        for (Object[] row : balancesData) {
            UUID accountId = (UUID) row[0];
            BigDecimal debit = (BigDecimal) row[1];
            BigDecimal credit = (BigDecimal) row[2];
            rawBalances.put(accountId, new BigDecimal[] {
                    debit != null ? debit : BigDecimal.ZERO,
                    credit != null ? credit : BigDecimal.ZERO
            });
        }

        List<AccountBalanceDTO> assets = new ArrayList<>();
        List<AccountBalanceDTO> liabilities = new ArrayList<>();
        List<AccountBalanceDTO> equity = new ArrayList<>();

        BigDecimal totalAssets = BigDecimal.ZERO;
        BigDecimal totalLiabilities = BigDecimal.ZERO;
        BigDecimal totalEquity = BigDecimal.ZERO;

        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal totalExpenses = BigDecimal.ZERO;

        // Process all accounts to ensure even zero-balance accounts are considered if
        // needed,
        // but typically we only want non-zero or we iterate active accounts.
        // For simplicity, let's iterate through the map of accounts that have balances
        // OR all accounts.
        // Doing all accounts ensures we list them even if zero.

        for (ChartOfAccount account : allAccounts) {
            BigDecimal[] raw = rawBalances.getOrDefault(account.getId(),
                    new BigDecimal[] { BigDecimal.ZERO, BigDecimal.ZERO });
            BigDecimal debit = raw[0];
            BigDecimal credit = raw[1];
            BigDecimal netBalance = BigDecimal.ZERO;

            switch (account.getAccountType()) {
                case ACTIVO:
                    netBalance = debit.subtract(credit);
                    if (netBalance.compareTo(BigDecimal.ZERO) != 0) {
                        assets.add(
                                new AccountBalanceDTO(account.getAccountCode(), account.getAccountName(), netBalance));
                        totalAssets = totalAssets.add(netBalance);
                    }
                    break;
                case PASIVO:
                    netBalance = credit.subtract(debit);
                    if (netBalance.compareTo(BigDecimal.ZERO) != 0) {
                        liabilities.add(
                                new AccountBalanceDTO(account.getAccountCode(), account.getAccountName(), netBalance));
                        totalLiabilities = totalLiabilities.add(netBalance);
                    }
                    break;
                case PATRIMONIO:
                    netBalance = credit.subtract(debit);
                    if (netBalance.compareTo(BigDecimal.ZERO) != 0) {
                        equity.add(
                                new AccountBalanceDTO(account.getAccountCode(), account.getAccountName(), netBalance));
                        totalEquity = totalEquity.add(netBalance);
                    }
                    break;
                case INGRESO:
                    netBalance = credit.subtract(debit);
                    totalRevenue = totalRevenue.add(netBalance);
                    break;
                case GASTO:
                    netBalance = debit.subtract(credit);
                    totalExpenses = totalExpenses.add(netBalance);
                    break;
            }
        }

        // Calculate Net Income (Resultado del Ejercicio)
        BigDecimal netIncome = totalRevenue.subtract(totalExpenses);

        // Add Net Income to Equity
        if (netIncome.compareTo(BigDecimal.ZERO) != 0) {
            equity.add(new AccountBalanceDTO("RESULTADO", "Resultado del Periodo", netIncome));
            totalEquity = totalEquity.add(netIncome);
        }

        return BalanceSheetDTO.builder()
                .assets(assets)
                .liabilities(liabilities)
                .equity(equity)
                .totalAssets(totalAssets)
                .totalLiabilities(totalLiabilities)
                .totalEquity(totalEquity)
                .build();
    }
}
