package com.microfinance.accounting_microservice.service;

import com.microfinance.accounting_microservice.domain.JournalEntry;
import com.microfinance.accounting_microservice.domain.Transaction;
import com.microfinance.accounting_microservice.domain.TransactionType;
import com.microfinance.accounting_microservice.dto.PaymentAppliedEvent;
import com.microfinance.accounting_microservice.repository.JournalEntryRepository;
import com.microfinance.accounting_microservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentAccountingService {

    private final TransactionRepository transactionRepository;
    private final JournalEntryRepository journalEntryRepository;

    @Transactional
    public Transaction recordPayment(PaymentAppliedEvent event) {
        LocalDateTime entryDate = event.getPaymentDate() != null ? event.getPaymentDate() : LocalDateTime.now();

        // 1) Registrar la transaccion principal
        Transaction tx = Transaction.builder()
                .id(UUID.randomUUID())
                .transactionType(TransactionType.PAGO_CAPITAL)
                .relatedEntityId(event.getLoanId())
                .amount(defaultZero(event.getTotalAmount()))
                .transactionDate(entryDate)
                .description(event.getDescription())
                .build();
        Transaction savedTx = transactionRepository.save(tx);

        // 2) Partidas dobles: Debito a caja/banco, credito a cuentas por cobrar y a ingresos
        BigDecimal total = defaultZero(event.getTotalAmount());
        BigDecimal capital = defaultZero(event.getCapitalAmount());
        BigDecimal interest = defaultZero(event.getInterestAmount());
        BigDecimal moratory = defaultZero(event.getMoratoryAmount());

        // Debito caja/banco por el total recibido
        journalEntryRepository.save(JournalEntry.builder()
                .id(UUID.randomUUID())
                .transactionId(savedTx.getId())
                .accountId(event.getCashAccountId())
                .debitAmount(total)
                .creditAmount(BigDecimal.ZERO)
                .entryDate(entryDate)
                .build());

        // Credito cuentas por cobrar (capital)
        if (capital.compareTo(BigDecimal.ZERO) > 0) {
            journalEntryRepository.save(JournalEntry.builder()
                    .id(UUID.randomUUID())
                    .transactionId(savedTx.getId())
                    .accountId(event.getLoanReceivableAccountId())
                    .debitAmount(BigDecimal.ZERO)
                    .creditAmount(capital)
                    .entryDate(entryDate)
                    .build());
        }

        // Credito ingreso por interes
        if (interest.compareTo(BigDecimal.ZERO) > 0) {
            journalEntryRepository.save(JournalEntry.builder()
                    .id(UUID.randomUUID())
                    .transactionId(savedTx.getId())
                    .accountId(event.getInterestIncomeAccountId())
                    .debitAmount(BigDecimal.ZERO)
                    .creditAmount(interest)
                    .entryDate(entryDate)
                    .build());
        }

        // Credito ingreso por mora
        if (moratory.compareTo(BigDecimal.ZERO) > 0) {
            journalEntryRepository.save(JournalEntry.builder()
                    .id(UUID.randomUUID())
                    .transactionId(savedTx.getId())
                    .accountId(event.getMoratoryIncomeAccountId())
                    .debitAmount(BigDecimal.ZERO)
                    .creditAmount(moratory)
                    .entryDate(entryDate)
                    .build());
        }

        return savedTx;
    }

    private BigDecimal defaultZero(BigDecimal val) {
        return val == null ? BigDecimal.ZERO : val;
    }
}
