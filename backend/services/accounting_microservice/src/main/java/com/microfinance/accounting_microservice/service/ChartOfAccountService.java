package com.microfinance.accounting_microservice.service;

import com.microfinance.accounting_microservice.dto.ChartOfAccountDTO;
import com.microfinance.accounting_microservice.domain.ChartOfAccount;
import com.microfinance.accounting_microservice.domain.AccountType;
import com.microfinance.accounting_microservice.repository.ChartOfAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChartOfAccountService {

    private final ChartOfAccountRepository chartOfAccountRepository;

    public ChartOfAccountDTO create(ChartOfAccountDTO dto) {
        ChartOfAccount account = ChartOfAccount.builder()
                .id(UUID.randomUUID())
                .accountCode(dto.getAccountCode())
                .accountName(dto.getAccountName())
                .accountType(AccountType.valueOf(dto.getAccountType()))
                .parentAccountId(dto.getParentAccountId())
                .deleted(false)
                .build();

        ChartOfAccount saved = chartOfAccountRepository.save(account);

        return toDTO(saved);
    }

    public List<ChartOfAccountDTO> findAll(Boolean deleted) {
        return chartOfAccountRepository.findAllByDeleted(deleted).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ChartOfAccountDTO findById(UUID id) {
        ChartOfAccount account = chartOfAccountRepository.findByIdAndDeleted(id, false)
                .orElseThrow(() -> new RuntimeException("Chart of account not found with id: " + id));
        return toDTO(account);
    }

    public ChartOfAccountDTO update(UUID id, ChartOfAccountDTO dto) {
        ChartOfAccount account = chartOfAccountRepository.findByIdAndDeleted(id, false)
                .orElseThrow(() -> new RuntimeException("Chart of account not found with id: " + id));
        
        account.setAccountCode(dto.getAccountCode());
        account.setAccountName(dto.getAccountName());
        account.setAccountType(AccountType.valueOf(dto.getAccountType()));
        account.setParentAccountId(dto.getParentAccountId());

        ChartOfAccount updated = chartOfAccountRepository.save(account);
        return toDTO(updated);
    }

    public void delete(UUID id) {
        ChartOfAccount account = chartOfAccountRepository.findByIdAndDeleted(id, false)
                .orElseThrow(() -> new RuntimeException("Chart of account not found with id: " + id));

        if (account.isDeleted()) {
            throw new IllegalStateException("La cuenta con id " + id + " ya está inactiva");
        }

        account.setDeleted(true);
        chartOfAccountRepository.save(account);
    }

    public void activate(UUID id) {
        ChartOfAccount account = chartOfAccountRepository.findByIdAndDeleted(id, true)
                .orElseThrow(() -> new RuntimeException("Chart of account not found with id: " + id));

        if (!account.isDeleted()) {
            throw new IllegalStateException("El id " + id + " ya está activo");
        }

        account.setDeleted(false);
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
