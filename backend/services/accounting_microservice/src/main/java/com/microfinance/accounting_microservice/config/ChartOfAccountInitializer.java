package com.microfinance.accounting_microservice.config;

import com.microfinance.accounting_microservice.repository.ChartOfAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Verifica que las cuentas contables con nombres lógicos requeridos existan.
 * No crea cuentas automáticamente - el usuario debe crearlas con los códigos
 * que correspondan a su plan contable específico.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChartOfAccountInitializer implements CommandLineRunner {

    private final ChartOfAccountRepository chartOfAccountRepository;

    private static final String[] REQUIRED_LOGICAL_NAMES = {
        "CASH_ACCOUNT",
        "LOAN_RECEIVABLE_ACCOUNT",
        "INTEREST_INCOME_ACCOUNT",
        "MORATORY_INCOME_ACCOUNT"
    };

    @Override
    public void run(String... args) {
        log.info("Verificando cuentas contables requeridas...");
        
        boolean allPresent = true;
        for (String logicalName : REQUIRED_LOGICAL_NAMES) {
            boolean exists = chartOfAccountRepository.findByLogicalNameIgnoreCaseAndDeleted(logicalName, false)
                .isPresent();
            
            if (exists) {
                log.debug("✓ Cuenta encontrada: {}", logicalName);
            } else {
                log.warn("✗ Cuenta faltante: {} - Debe crear esta cuenta con el código que corresponda a su plan contable", logicalName);
                allPresent = false;
            }
        }
        
        if (allPresent) {
            log.info("✓ Todas las cuentas contables requeridas están configuradas.");
        } else {
            log.warn("⚠ Faltan cuentas contables. Por favor, cree las cuentas con los siguientes nombres lógicos:");
            for (String logicalName : REQUIRED_LOGICAL_NAMES) {
                boolean exists = chartOfAccountRepository.findByLogicalNameIgnoreCaseAndDeleted(logicalName, false)
                    .isPresent();
                if (!exists) {
                    log.warn("  - {} (ej: CASH_ACCOUNT)", logicalName);
                }
            }
            log.warn("Puede crear las cuentas mediante POST /api/chart-of-accounts con el campo 'logicalName' correspondiente.");
        }
    }
}

