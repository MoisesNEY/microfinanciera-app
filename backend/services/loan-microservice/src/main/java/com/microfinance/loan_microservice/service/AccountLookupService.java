package com.microfinance.loan_microservice.service;

import com.microfinance.loan_microservice.config.AccountingProperties;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servicio para consultar cuentas contables del accounting-microservice
 * por nombre lógico, código o nombre. Implementa cache para evitar consultas
 * repetidas.
 */
@Slf4j
@Service
public class AccountLookupService {

    private final RestTemplate restTemplate;
    private final AccountingProperties props;
    private final Map<String, UUID> cache = new ConcurrentHashMap<>();

    public AccountLookupService(RestTemplate restTemplate, AccountingProperties props) {
        this.restTemplate = restTemplate;
        this.props = props;
    }

    /**
     * Obtiene el UUID de una cuenta contable por su nombre lógico, código o nombre.
     * Orden de búsqueda: 1) Nombre lógico, 2) Código, 3) Nombre.
     * Usa cache para evitar consultas repetidas al accounting-microservice.
     * 
     * @param logicalNameOrCodeOrName Nombre lógico (ej: "CASH_ACCOUNT"), código
     *                                (ej: "1001") o nombre de la cuenta
     * @param bearerToken             Token de autenticación (opcional)
     * @return UUID de la cuenta
     * @throws IllegalStateException si la cuenta no se encuentra
     */
    @CircuitBreaker(name = "accounting-service", fallbackMethod = "lookupAccountIdFallback")
    public UUID lookupAccountId(String logicalNameOrCodeOrName, String bearerToken) {
        if (logicalNameOrCodeOrName == null || logicalNameOrCodeOrName.isBlank()) {
            throw new IllegalStateException("Nombre lógico, código o nombre de cuenta no puede ser nulo o vacío");
        }

        // Verificar cache
        UUID cached = cache.get(logicalNameOrCodeOrName);
        if (cached != null) {
            log.debug("Cuenta {} encontrada en cache: {}", logicalNameOrCodeOrName, cached);
            return cached;
        }

        // Consultar al accounting-microservice
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (bearerToken != null && !bearerToken.isBlank()) {
                String tokenSolo = bearerToken.replaceFirst("(?i)^Bearer ", "");
                headers.setBearerAuth(tokenSolo);
            }

            String url = props.getService().getUrl() + "/api/chart-of-accounts/lookup/" + logicalNameOrCodeOrName;
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    Map.class).getBody();

            if (response != null && response.containsKey("id")) {
                String idStr = response.get("id").toString();
                UUID accountId = UUID.fromString(idStr);
                cache.put(logicalNameOrCodeOrName, accountId);
                log.debug("Cuenta {} resuelta a UUID: {}", logicalNameOrCodeOrName, accountId);
                return accountId;
            }

            throw new IllegalStateException(
                    "No se encontró cuenta contable con nombre lógico, código o nombre: " + logicalNameOrCodeOrName);
        } catch (org.springframework.web.client.HttpClientErrorException.NotFound e) {
            throw new IllegalStateException(
                    "No se encontró cuenta contable con nombre lógico, código o nombre: " + logicalNameOrCodeOrName +
                            ". Verifique que la cuenta existe en el accounting-microservice.",
                    e);
        }
    }

    @SuppressWarnings("unused")
    private UUID lookupAccountIdFallback(String logicalNameOrCodeOrName, String bearerToken, Throwable ex) {
        log.error("Circuit breaker activado para lookupAccountId: {}", ex.getMessage());
        throw new IllegalStateException("Servicio de contabilidad no disponible", ex);
    }

    /**
     * Limpia el cache. Útil para forzar la actualización de cuentas.
     */
    public void clearCache() {
        cache.clear();
        log.debug("Cache de AccountLookupService limpiado");
    }

    /**
     * Limpia el cache de un nombre lógico/código/nombre específico.
     */
    public void clearCache(String logicalNameOrCodeOrName) {
        cache.remove(logicalNameOrCodeOrName);
    }
}
