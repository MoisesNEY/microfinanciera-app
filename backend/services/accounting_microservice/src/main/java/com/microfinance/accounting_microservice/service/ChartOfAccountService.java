package com.microfinance.accounting_microservice.service;

import com.microfinance.accounting_microservice.dto.ChartOfAccountDTO;
import com.microfinance.accounting_microservice.domain.ChartOfAccount;
import com.microfinance.accounting_microservice.domain.AccountType;
import com.microfinance.accounting_microservice.repository.ChartOfAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChartOfAccountService {

    private final ChartOfAccountRepository chartOfAccountRepository;

    public ChartOfAccountDTO create(ChartOfAccountDTO dto) {
        ChartOfAccount account = ChartOfAccount.builder()
                .accountCode(dto.getAccountCode())
                .accountName(dto.getAccountName())
                .accountType(AccountType.valueOf(dto.getAccountType()))
                .parentAccountId(dto.getParentAccountId())
                .build();

        ChartOfAccount saved = chartOfAccountRepository.save(account);

        return ChartOfAccountDTO.builder()
                .id(saved.getId())
                .accountCode(saved.getAccountCode())
                .accountName(saved.getAccountName())
                .accountType(saved.getAccountType().name())
                .parentAccountId(saved.getParentAccountId())
                .build();
    }

    public List<ChartOfAccountDTO> findAll() {
        return chartOfAccountRepository.findAll().stream()
                .map(c -> ChartOfAccountDTO.builder()
                        .id(c.getId())
                        .accountCode(c.getAccountCode())
                        .accountName(c.getAccountName())
                        .accountType(c.getAccountType().name())
                        .parentAccountId(c.getParentAccountId())
                        .build())
                .collect(Collectors.toList());
    }
}
