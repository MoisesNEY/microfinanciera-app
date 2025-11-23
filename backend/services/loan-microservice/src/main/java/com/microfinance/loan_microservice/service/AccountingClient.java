package com.microfinance.loan_microservice.service;

import com.microfinance.loan_microservice.config.AccountingProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class AccountingClient {

    private final RestTemplate restTemplate;
    private final AccountingProperties props;

    public AccountingClient(RestTemplate restTemplate, AccountingProperties props) {
        this.restTemplate = restTemplate;
        this.props = props;
    }

    public void sendPaymentApplied(UUID paymentId,
                                UUID loanId,
                                BigDecimal capital,
                                BigDecimal interest,
                                BigDecimal moratory,
                                BigDecimal total,
                                LocalDate paymentDate,
                                String description,
                                String bearerToken) {
        var accounts = props.getAccounts();
        validateAccountsConfigured(accounts);

        Map<String, Object> body = new HashMap<>();
        body.put("paymentId", paymentId);
        body.put("loanId", loanId);
        body.put("capitalAmount", capital);
        body.put("interestAmount", interest);
        body.put("moratoryAmount", moratory);
        body.put("totalAmount", total);
        body.put("paymentDate", paymentDate);
        body.put("description", description);
        body.put("cashAccountId", accounts.getCash());
        body.put("loanReceivableAccountId", accounts.getLoanReceivable());
        body.put("interestIncomeAccountId", accounts.getInterestIncome());
        body.put("moratoryIncomeAccountId", accounts.getMoratoryIncome());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (bearerToken != null && !bearerToken.isBlank()) {
            String tokenSolo = bearerToken.replaceFirst("(?i)^Bearer ", "");
            headers.setBearerAuth(tokenSolo);
        }

        restTemplate.postForEntity(props.getService().getUrl() + "/api/accounting/payment-applied", new HttpEntity<>(body, headers), Void.class);
    }

    private void validateAccountsConfigured(AccountingProperties.Accounts accounts) {
        if (accounts == null) {
            throw new IllegalStateException("Config accounting.accounts no está definida");
        }
        if (accounts.getCash() == null || accounts.getCash() <= 0) {
            throw new IllegalStateException("Config accounting.accounts.cash es requerida y debe ser > 0");
        }
        if (accounts.getLoanReceivable() == null || accounts.getLoanReceivable() <= 0) {
            throw new IllegalStateException("Config accounting.accounts.loanReceivable es requerida y debe ser > 0");
        }
        if (accounts.getInterestIncome() == null || accounts.getInterestIncome() <= 0) {
            throw new IllegalStateException("Config accounting.accounts.interestIncome es requerida y debe ser > 0");
        }
        if (accounts.getMoratoryIncome() == null || accounts.getMoratoryIncome() <= 0) {
            throw new IllegalStateException("Config accounting.accounts.moratoryIncome es requerida y debe ser > 0");
        }
    }
}
