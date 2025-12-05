package com.microfinance.loan_microservice.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class CustomerCircuitService {

    private final CustomerServiceClient customerServiceClient;


    public CustomerCircuitService(CustomerServiceClient customerServiceClient) {
        this.customerServiceClient = customerServiceClient;
    }

    @CircuitBreaker(name = "customer-service", fallbackMethod = "getClientByIdCircuitFallback")
    public Map<String, Object> getClientByIdCircuit(UUID clientId) {
        return customerServiceClient.getClientById(clientId);
    }

    @SuppressWarnings("unused")
    private Map<String, Object> getClientByIdCircuitFallback(UUID clientId, Throwable ex) {
        Map<String, Object> fallback = new HashMap<>();
        fallback.put("id", clientId.toString());
        fallback.put("error", "Customer service no disponible (circuit breaker)");
        fallback.put("fallback", true);
        fallback.put("details", ex.getMessage());
        return fallback;
    }

}
