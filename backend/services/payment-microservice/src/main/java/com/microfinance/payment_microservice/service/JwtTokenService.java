package com.microfinance.payment_microservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.UUID;

/**
 * Servicio para extraer información del token JWT de Keycloak.
 * Extrae el ID del trabajador (keycloakId) del token y lo convierte al UUID interno.
 */
@Service
public class JwtTokenService {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenService.class);
    private static final String SUB_CLAIM = "sub"; // Claim estándar de Keycloak que contiene el ID del usuario

    private final WorkerServiceClient workerServiceClient;

    public JwtTokenService(WorkerServiceClient workerServiceClient) {
        this.workerServiceClient = workerServiceClient;
    }

    /**
     * Obtiene el keycloakId (sub) del token JWT actual.
     * 
     * @return El keycloakId del usuario autenticado, o null si no está disponible
     */
    public String getKeycloakIdFromToken() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            log.debug("Authentication type: {}", authentication != null ? authentication.getClass().getName() : "null");
            
            if (authentication == null) {
                log.warn("SecurityContext no tiene Authentication. ¿La seguridad está deshabilitada?");
                return null;
            }
            
            if (authentication instanceof JwtAuthenticationToken) {
                JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) authentication;
                Jwt jwt = jwtAuth.getToken();
                
                if (jwt != null) {
                    String sub = jwt.getClaimAsString(SUB_CLAIM);
                    if (sub != null && !sub.isBlank()) {
                        log.info("KeycloakId extraído del token: {}", sub);
                        return sub;
                    } else {
                        log.warn("El token JWT no tiene el claim 'sub' o está vacío");
                    }
                } else {
                    log.warn("El JwtAuthenticationToken no tiene un JWT asociado");
                }
            } else {
                log.warn("La autenticación no es de tipo JwtAuthenticationToken. Tipo: {}", authentication.getClass().getName());
            }
        } catch (Exception e) {
            log.error("Error al extraer keycloakId del token JWT: {}", e.getMessage(), e);
        }
        
        log.error("No se pudo extraer el keycloakId del token JWT");
        return null;
    }

    /**
     * Obtiene el UUID interno del trabajador desde el token JWT.
     * Primero extrae el keycloakId del token, luego consulta el microservicio de workers
     * para obtener el UUID interno del trabajador.
     * 
     * @param bearerToken Token Bearer para autenticación en el microservicio de workers
     * @return El UUID interno del trabajador
     * @throws IllegalStateException si no se puede obtener el keycloakId del token
     */
    public UUID getWorkerIdFromToken(String bearerToken) {
        // Intentar obtener el keycloakId del SecurityContext primero
        String keycloakId = getKeycloakIdFromToken();
        
        // Si no se pudo obtener del SecurityContext, intentar extraerlo del bearerToken directamente
        if (keycloakId == null || keycloakId.isBlank()) {
            log.warn("No se pudo obtener keycloakId del SecurityContext, intentando extraerlo del bearerToken");
            keycloakId = extractKeycloakIdFromBearerToken(bearerToken);
        }
        
        if (keycloakId == null || keycloakId.isBlank()) {
            log.error("No se pudo extraer el keycloakId del token JWT ni del bearerToken");
            throw new IllegalStateException(
                "No se pudo extraer el keycloakId del token JWT. " +
                "Asegúrate de que el usuario esté autenticado correctamente y que el token JWT sea válido."
            );
        }

        log.info("KeycloakId obtenido: {}", keycloakId);
        
        try {
            UUID workerId = workerServiceClient.getWorkerIdByKeycloakId(keycloakId, bearerToken);
            log.info("WorkerId obtenido para keycloakId {}: {}", keycloakId, workerId);
            return workerId;
        } catch (Exception e) {
            log.error("Error al obtener el workerId para keycloakId {}: {}", keycloakId, e.getMessage(), e);
            throw new IllegalStateException(
                "No se pudo obtener el ID del trabajador desde el microservicio de workers. " +
                "Asegúrate de que el usuario existe en el sistema. Error: " + e.getMessage(),
                e
            );
        }
    }
    
    /**
     * Intenta extraer el keycloakId del bearerToken directamente decodificando el JWT.
     * Esto es un fallback si el SecurityContext no tiene el token.
     */
    private String extractKeycloakIdFromBearerToken(String bearerToken) {
        if (bearerToken == null || bearerToken.isBlank()) {
            log.warn("BearerToken es null o está vacío");
            return null;
        }
        
        try {
            // Remover el prefijo "Bearer " si existe
            String token = bearerToken.replaceFirst("(?i)^Bearer ", "");
            log.debug("Token sin prefijo Bearer (primeros 50 chars): {}", 
                     token.length() > 50 ? token.substring(0, 50) + "..." : token);
            
            // Decodificar el JWT (solo la parte del payload, sin verificar firma)
            String[] parts = token.split("\\.");
            log.debug("JWT tiene {} partes", parts.length);
            
            if (parts.length < 3) {
                log.warn("El token JWT no tiene el formato correcto. Se esperan 3 partes separadas por '.', pero se encontraron {}", parts.length);
                return null;
            }
            
            if (parts.length >= 2) {
                // Decodificar el payload (base64url)
                String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
                log.info("JWT payload decodificado: {}", payload);
                
                // Parsear el JSON para obtener el "sub"
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(payload);
                JsonNode subNode = jsonNode.get("sub");
                
                if (subNode != null && !subNode.asText().isBlank()) {
                    String sub = subNode.asText();
                    log.info("KeycloakId extraído del bearerToken: {}", sub);
                    return sub;
                } else {
                    log.warn("El payload JWT no contiene el claim 'sub' o está vacío. Payload: {}", payload);
                }
            }
        } catch (IllegalArgumentException e) {
            log.error("Error al decodificar Base64 del bearerToken: {}", e.getMessage(), e);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            log.error("Error al parsear JSON del payload JWT: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error inesperado al decodificar el bearerToken: {}", e.getMessage(), e);
        }
        
        return null;
    }

    /**
     * Obtiene el token JWT completo del contexto de seguridad.
     * 
     * @return El token JWT como string, o null si no está disponible
     */
    public String getTokenValue() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication != null && authentication instanceof JwtAuthenticationToken) {
                JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) authentication;
                Jwt jwt = jwtAuth.getToken();
                
                if (jwt != null) {
                    return jwt.getTokenValue();
                }
            }
        } catch (Exception e) {
            log.debug("No se pudo obtener el token del SecurityContext: {}", e.getMessage());
        }
        return null;
    }
}

