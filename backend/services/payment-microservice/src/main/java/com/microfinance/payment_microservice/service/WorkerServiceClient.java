package com.microfinance.payment_microservice.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.UUID;

/**
 * Cliente para comunicarse con el microservicio de workers.
 * Obtiene el UUID interno del trabajador basado en su keycloakId.
 */
@Component
public class WorkerServiceClient {

    private static final Logger log = LoggerFactory.getLogger(WorkerServiceClient.class);
    
    private final RestTemplate restTemplate;
    private final String workerServiceUrl;

    public WorkerServiceClient(
            RestTemplate restTemplate,
            @Value("${worker.service.url:http://api-gateway:8080}") String workerServiceUrl) {
        this.restTemplate = restTemplate;
        this.workerServiceUrl = workerServiceUrl;
    }

    /**
     * Obtiene el UUID interno del trabajador basado en su keycloakId.
     * 
     * @param keycloakId El ID del usuario en Keycloak (sub claim)
     * @param bearerToken Token Bearer para autenticación
     * @return El UUID interno del trabajador
     * @throws IllegalStateException si el trabajador no se encuentra
     */
    @CircuitBreaker(name = "worker-service", fallbackMethod = "getWorkerIdByKeycloakIdFallback")
    public UUID getWorkerIdByKeycloakId(String keycloakId, String bearerToken) {
        String url = UriComponentsBuilder
                .fromHttpUrl(workerServiceUrl)
                .path("/workers/by-keycloak-id/{keycloakId}")
                .buildAndExpand(keycloakId)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        if (bearerToken != null && !bearerToken.isBlank()) {
            String tokenSolo = bearerToken.replaceFirst("(?i)^Bearer ", "");
            headers.setBearerAuth(tokenSolo);
        }
        
        HttpEntity<Void> request = new HttpEntity<>(headers);
        
        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    Map.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> worker = response.getBody();
                Object idObj = worker.get("id");
                
                if (idObj != null) {
                    if (idObj instanceof UUID) {
                        return (UUID) idObj;
                    } else if (idObj instanceof String) {
                        return UUID.fromString((String) idObj);
                    }
                }
            }
            
            throw new IllegalStateException("El trabajador con keycloakId " + keycloakId + " no tiene un ID válido");
        } catch (org.springframework.web.client.HttpClientErrorException.NotFound e) {
            throw new IllegalStateException(
                "No se encontró un trabajador con keycloakId: " + keycloakId + 
                ". Asegúrate de que el usuario existe en el sistema de workers.",
                e
            );
        } catch (Exception e) {
            log.error("Error al obtener worker por keycloakId {}: {}", keycloakId, e.getMessage());
            throw new IllegalStateException(
                "Error al comunicarse con el microservicio de workers: " + e.getMessage(),
                e
            );
        }
    }

    @SuppressWarnings("unused")
    private UUID getWorkerIdByKeycloakIdFallback(String keycloakId, String bearerToken, Throwable ex) {
        log.error("Fallback: No se pudo obtener el workerId para keycloakId {}: {}", keycloakId, ex.getMessage());
        throw new IllegalStateException(
            "El microservicio de workers no está disponible. No se puede obtener el ID del trabajador.",
            ex
        );
    }
}

