package com.microfinance.accounting_microservice.service;

import com.microfinance.accounting_microservice.dto.ChartOfAccountDTO;
import com.microfinance.accounting_microservice.domain.ChartOfAccount;
import com.microfinance.accounting_microservice.domain.AccountType;
import com.microfinance.accounting_microservice.repository.ChartOfAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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
                .logicalName(dto.getLogicalName())
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
        account.setLogicalName(dto.getLogicalName());
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

    /**
     * Busca una cuenta por código.
     * 
     * @param accountCode Código de la cuenta
     * @return DTO de la cuenta encontrada
     * @throws RuntimeException si no se encuentra la cuenta
     */
    public ChartOfAccountDTO findByCode(String accountCode) {
        ChartOfAccount account = chartOfAccountRepository.findByAccountCodeAndDeleted(accountCode, false)
                .orElseThrow(() -> new RuntimeException("Chart of account not found with code: " + accountCode));
        return toDTO(account);
    }

    /**
     * Busca una cuenta por nombre (búsqueda case-insensitive).
     * 
     * @param accountName Nombre de la cuenta
     * @return DTO de la cuenta encontrada
     * @throws RuntimeException si no se encuentra la cuenta
     */
    public ChartOfAccountDTO findByName(String accountName) {
        ChartOfAccount account = chartOfAccountRepository.findByAccountNameIgnoreCaseAndDeleted(accountName, false)
                .orElseThrow(() -> new RuntimeException("Chart of account not found with name: " + accountName));
        return toDTO(account);
    }

    /**
     * Busca una cuenta por nombre lógico (case-insensitive).
     * 
     * @param logicalName Nombre lógico de la cuenta (ej: "CASH_ACCOUNT")
     * @return DTO de la cuenta encontrada
     * @throws RuntimeException si no se encuentra la cuenta
     */
    public ChartOfAccountDTO findByLogicalName(String logicalName) {
        ChartOfAccount account = chartOfAccountRepository.findByLogicalNameIgnoreCaseAndDeleted(logicalName, false)
                .orElseThrow(() -> new RuntimeException("Chart of account not found with logical name: " + logicalName));
        return toDTO(account);
    }

    /**
     * Busca una cuenta por nombre lógico, código o nombre.
     * Orden de búsqueda: 1) Nombre lógico, 2) Código, 3) Nombre.
     * 
     * @param logicalNameOrCodeOrName Nombre lógico, código o nombre de la cuenta
     * @return DTO de la cuenta encontrada
     * @throws RuntimeException si no se encuentra la cuenta
     */
    public ChartOfAccountDTO findByCodeOrName(String logicalNameOrCodeOrName) {
        // 1) Primero intentar por nombre lógico (más semántico)
        Optional<ChartOfAccount> byLogicalName = chartOfAccountRepository.findByLogicalNameIgnoreCaseAndDeleted(logicalNameOrCodeOrName, false);
        if (byLogicalName.isPresent()) {
            return toDTO(byLogicalName.get());
        }
        
        // 2) Si no se encuentra por nombre lógico, intentar por código
        Optional<ChartOfAccount> byCode = chartOfAccountRepository.findByAccountCodeAndDeleted(logicalNameOrCodeOrName, false);
        if (byCode.isPresent()) {
            return toDTO(byCode.get());
        }
        
        // 3) Si no se encuentra por código, intentar por nombre
        Optional<ChartOfAccount> byName = chartOfAccountRepository.findByAccountNameIgnoreCaseAndDeleted(logicalNameOrCodeOrName, false);
        if (byName.isPresent()) {
            return toDTO(byName.get());
        }
        
        throw new RuntimeException("Chart of account not found with logical name, code or name: " + logicalNameOrCodeOrName);
    }

    private ChartOfAccountDTO toDTO(ChartOfAccount account) {
        return ChartOfAccountDTO.builder()
                .id(account.getId())
                .accountCode(account.getAccountCode())
                .accountName(account.getAccountName())
                .logicalName(account.getLogicalName())
                .accountType(account.getAccountType().name())
                .parentAccountId(account.getParentAccountId())
                .build();
    }
}
