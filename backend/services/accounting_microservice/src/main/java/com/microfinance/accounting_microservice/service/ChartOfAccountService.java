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
                .deleted(false)
                .build();

        ChartOfAccount saved = chartOfAccountRepository.save(account);

        return toDTO(saved);
    }

    public List<ChartOfAccountDTO> findAll() {
        return chartOfAccountRepository.findAllByDeletedFalse().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ChartOfAccountDTO findById(Integer id) {
        ChartOfAccount account = chartOfAccountRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Chart of account not found with id: " + id));
        return toDTO(account);
    }

    public ChartOfAccountDTO update(Integer id, ChartOfAccountDTO dto) {
        ChartOfAccount account = chartOfAccountRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Chart of account not found with id: " + id));
        
        account.setAccountCode(dto.getAccountCode());
        account.setAccountName(dto.getAccountName());
        account.setAccountType(AccountType.valueOf(dto.getAccountType()));
        account.setParentAccountId(dto.getParentAccountId());

        ChartOfAccount updated = chartOfAccountRepository.save(account);
        return toDTO(updated);
    }

    public void delete(Integer id) {
        ChartOfAccount account = chartOfAccountRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Chart of account not found with id: " + id));
        account.setDeleted(true);
        chartOfAccountRepository.save(account);
    }

    private ChartOfAccountDTO toDTO(ChartOfAccount account) {
        return ChartOfAccountDTO.builder()
                .id(account.getId())
                .accountCode(account.getAccountCode())
                .accountName(account.getAccountName())
                .accountType(account.getAccountType().name())
                .parentAccountId(account.getParentAccountId())
                .build();
    }
}
