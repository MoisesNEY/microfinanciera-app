package com.microfinance.payment_microservice.service;

import com.microfinance.payment_microservice.domain.Payment;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class LoanPaymentClient {

  private final RestTemplate restTemplate;
  private final String loanServiceUrl;

  public LoanPaymentClient(RestTemplate restTemplate,
      @Value("${loan.service.url:http://api-gateway:8080}") String loanServiceUrl) {
    this.restTemplate = restTemplate;
    this.loanServiceUrl = loanServiceUrl;
  }

  @CircuitBreaker(name = "loan-service", fallbackMethod = "applyPaymentFallback")
  public void applyPayment(Payment payment, String bearerToken) {
    String url = loanServiceUrl + "/api/loan-payments";
    Map<String, Object> body = new HashMap<>();
    body.put("loanId", payment.getLoanId());
    body.put("installmentId", null); // opcional
    body.put("paymentDate", payment.getPaymentDate());
    body.put("amount", payment.getAmountPaid());
    body.put("method", payment.getPaymentMethod().name());
    body.put("reference", payment.getTransactionReference());

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    if (bearerToken != null && !bearerToken.isBlank()) {
      String tokenSolo = bearerToken.replaceFirst("(?i)^Bearer ", "");
      headers.setBearerAuth(tokenSolo);
    }
    HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
    restTemplate.postForEntity(url, request, Void.class);
  }

  @SuppressWarnings("unused")
  private void applyPaymentFallback(Payment payment, String bearerToken, Throwable ex) {
    throw new IllegalStateException(
        "Loan service no disponible al aplicar pago para loanId " + payment.getLoanId(),
        ex);
  }
}
