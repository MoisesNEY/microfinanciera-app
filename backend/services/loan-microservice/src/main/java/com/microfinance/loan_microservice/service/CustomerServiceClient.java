package com.microfinance.loan_microservice.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "api-gateway", url = "${api.gateway.url:http://api-gateway:8080}"
// fallback = CustomerServiceClientFallback.class
)
public interface CustomerServiceClient {

    @GetMapping("/api/clients/{clientId}")
    Map<String, Object> getClientById(@PathVariable("clientId") UUID clientId);

    // Opcional: endpoint para información básica
    @GetMapping("/api/clients/{clientId}/basic")
    Map<String, Object> getClientBasicInfo(@PathVariable("clientId") UUID clientId);

}
