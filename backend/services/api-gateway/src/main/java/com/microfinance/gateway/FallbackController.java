// backend/services/api-gateway/src/main/java/com/microfinance/gateway/FallbackController.java
package com.microfinance.gateway;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import java.util.Map;

@RestController
public class FallbackController {
  @GetMapping(value = "/fallback/loans", produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<Map<String,Object>> loansFallback() {
    return Mono.just(Map.of(
        "service", "loan-microservice",
        "status", "unavailable",
        "message", "Servicio de préstamos temporalmente no disponible. Intenta de nuevo."
    ));
  }
}
