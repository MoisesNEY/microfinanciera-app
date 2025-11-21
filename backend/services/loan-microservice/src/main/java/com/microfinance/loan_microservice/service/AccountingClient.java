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
                                String description) {
        var accounts = props.getAccounts();
        if (accounts.getCash() == null || accounts.getLoanReceivable() == null
            || accounts.getInterestIncome() == null || accounts.getMoratoryIncome() == null) {
            // Config incompleta: no enviamos nada
            return;
        }

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

        restTemplate.postForEntity(props.getService().getUrl() + "/api/accounting/payment-applied", new HttpEntity<>(body, headers), Void.class);
    }
}
