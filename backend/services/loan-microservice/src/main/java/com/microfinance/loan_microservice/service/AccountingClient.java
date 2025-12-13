package com.microfinance.loan_microservice.service;

import com.microfinance.loan_microservice.config.AccountingProperties;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class AccountingClient {

    private final RestTemplate restTemplate;
    private final AccountingProperties props;
    private final AccountLookupService accountLookupService;

    public AccountingClient(RestTemplate restTemplate, AccountingProperties props,
            AccountLookupService accountLookupService) {
        this.restTemplate = restTemplate;
        this.props = props;
        this.accountLookupService = accountLookupService;
    }

    @CircuitBreaker(name = "accounting-service", fallbackMethod = "sendPaymentAppliedFallback")
    public void sendPaymentApplied(UUID paymentId,
            UUID loanId,
            BigDecimal capital,
            BigDecimal interest,
            BigDecimal moratory,
            BigDecimal total,
            ZonedDateTime paymentDate,
            String description,
            String bearerToken) {
        var accounts = props.getAccounts();
        validateAccountsConfigured(accounts);

        // Resolver códigos/nombres de cuentas a UUIDs consultando al
        // accounting-microservice
        UUID cashAccountId = accountLookupService.lookupAccountId(accounts.getCash(), bearerToken);
        UUID loanReceivableAccountId = accountLookupService.lookupAccountId(accounts.getLoanReceivable(), bearerToken);
        UUID interestIncomeAccountId = accountLookupService.lookupAccountId(accounts.getInterestIncome(), bearerToken);
        UUID moratoryIncomeAccountId = accountLookupService.lookupAccountId(accounts.getMoratoryIncome(), bearerToken);

        Map<String, Object> body = new HashMap<>();
        body.put("paymentId", paymentId);
        body.put("loanId", loanId);
        body.put("capitalAmount", capital);
        body.put("interestAmount", interest);
        body.put("moratoryAmount", moratory);
        body.put("totalAmount", total);
        body.put("paymentDate", paymentDate);
        body.put("description", description);
        body.put("cashAccountId", cashAccountId);
        body.put("loanReceivableAccountId", loanReceivableAccountId);
        body.put("interestIncomeAccountId", interestIncomeAccountId);
        body.put("moratoryIncomeAccountId", moratoryIncomeAccountId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (bearerToken != null && !bearerToken.isBlank()) {
            String tokenSolo = bearerToken.replaceFirst("(?i)^Bearer ", "");
            headers.setBearerAuth(tokenSolo);
        }

        restTemplate.postForEntity(props.getService().getUrl() + "/api/accounting/payment-applied",
                new HttpEntity<>(body, headers), Void.class);
    }

    @SuppressWarnings("unused")
    private void sendPaymentAppliedFallback(UUID paymentId,
            UUID loanId,
            BigDecimal capital,
            BigDecimal interest,
            BigDecimal moratory,
            BigDecimal total,
            ZonedDateTime paymentDate,
            String description,
            String bearerToken,
            Throwable ex) {
        log.error("Circuit breaker activado para sendPaymentApplied: {}", ex.getMessage());
        throw new IllegalStateException("Servicio de contabilidad no disponible. No se puede procesar el pago.", ex);
    }

    private void validateAccountsConfigured(AccountingProperties.Accounts accounts) {
        if (accounts == null) {
            throw new IllegalStateException("Config accounting.accounts no está definida");
        }
        if (accounts.getCash() == null || accounts.getCash().isBlank()) {
            throw new IllegalStateException("Config accounting.accounts.cash (código o nombre) es requerida");
        }
        if (accounts.getLoanReceivable() == null || accounts.getLoanReceivable().isBlank()) {
            throw new IllegalStateException("Config accounting.accounts.loanReceivable (código o nombre) es requerida");
        }
        if (accounts.getInterestIncome() == null || accounts.getInterestIncome().isBlank()) {
            throw new IllegalStateException("Config accounting.accounts.interestIncome (código o nombre) es requerida");
        }
        if (accounts.getMoratoryIncome() == null || accounts.getMoratoryIncome().isBlank()) {
            throw new IllegalStateException("Config accounting.accounts.moratoryIncome (código o nombre) es requerida");
        }
    }
}
