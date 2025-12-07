package com.microfinance.loan_microservice.service;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.UUID;

/**
 * Servicio para extraer información del token JWT de Keycloak.
 * Extrae el ID del trabajador (keycloakId) del token y lo convierte al UUID interno.
 * 
 * Soporta dos modos:
 * 1. Obtener el token del SecurityContext (cuando Spring Security lo ha procesado)
 * 2. Decodificar manualmente el bearerToken proporcionado como parámetro
 */
@Service
public class JwtTokenService {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenService.class);
    private static final String SUB_CLAIM = "sub"; // Claim estándar de Keycloak que contiene el ID del usuario
    private static final String BEARER_PREFIX = "Bearer ";

    private final WorkerServiceClient workerServiceClient;
    private final JwtDecoder jwtDecoder;

    public JwtTokenService(
            WorkerServiceClient workerServiceClient,
            @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri:}") String issuerUri) {
        this.workerServiceClient = workerServiceClient;
        
        // Configurar JwtDecoder si tenemos issuer-uri, sino será null
        if (issuerUri != null && !issuerUri.isBlank()) {
            this.jwtDecoder = NimbusJwtDecoder.withIssuerLocation(issuerUri).build();
        } else {
            this.jwtDecoder = null;
            log.warn("JwtDecoder no configurado. La decodificación manual de tokens puede no funcionar correctamente.");
        }
    }

    /**
     * Obtiene el keycloakId (sub) del token JWT.
     * Primero intenta obtenerlo del SecurityContext, si no está disponible,
     * intenta decodificar el bearerToken proporcionado.
     * 
     * @param bearerToken Token JWT opcional (formato "Bearer <token>" o solo "<token>")
     * @return El keycloakId del usuario autenticado, o null si no está disponible
     */
    public String getKeycloakIdFromToken(String bearerToken) {
        // Intentar obtener del SecurityContext primero
        String keycloakId = getKeycloakIdFromSecurityContext();
        if (keycloakId != null) {
            return keycloakId;
        }
        
        // Si no está en SecurityContext y se proporcionó bearerToken, decodificarlo manualmente
        if (bearerToken != null && !bearerToken.isBlank()) {
            keycloakId = getKeycloakIdFromBearerToken(bearerToken);
            if (keycloakId != null) {
                return keycloakId;
            }
        }
        
        log.error("No se pudo extraer el keycloakId del token JWT");
        return null;
    }
    
    /**
     * Obtiene el keycloakId del SecurityContext de Spring Security.
     */
    private String getKeycloakIdFromSecurityContext() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            log.debug("Authentication type: {}", authentication != null ? authentication.getClass().getName() : "null");
            
            if (authentication == null) {
                log.debug("SecurityContext no tiene Authentication");
                return null;
            }
            
            if (authentication instanceof JwtAuthenticationToken) {
                JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) authentication;
                Jwt jwt = jwtAuth.getToken();
                
                if (jwt != null) {
                    String sub = jwt.getClaimAsString(SUB_CLAIM);
                    if (sub != null && !sub.isBlank()) {
                        log.debug("KeycloakId extraído del SecurityContext: {}", sub);
                        return sub;
                    }
                }
            } else {
                log.debug("La autenticación no es de tipo JwtAuthenticationToken. Tipo: {}", authentication.getClass().getName());
            }
        } catch (Exception e) {
            log.debug("Error al extraer keycloakId del SecurityContext: {}", e.getMessage());
        }
        return null;
    }
    
    /**
     * Decodifica manualmente el bearerToken y extrae el keycloakId.
     */
    private String getKeycloakIdFromBearerToken(String bearerToken) {
        try {
            // Remover el prefijo "Bearer " si existe
            String tokenValue = bearerToken.startsWith(BEARER_PREFIX) 
                ? bearerToken.substring(BEARER_PREFIX.length()) 
                : bearerToken;
            
            // Intentar decodificar con JwtDecoder si está disponible
            if (jwtDecoder != null) {
                try {
                    Jwt jwt = jwtDecoder.decode(tokenValue);
                    String sub = jwt.getClaimAsString(SUB_CLAIM);
                    if (sub != null && !sub.isBlank()) {
                        log.debug("KeycloakId extraído del bearerToken (JwtDecoder): {}", sub);
                        return sub;
                    }
                } catch (Exception e) {
                    log.debug("Error al decodificar con JwtDecoder: {}", e.getMessage());
                }
            }
            
            // Fallback: decodificar manualmente usando Nimbus JWT
            JWT jwt = JWTParser.parse(tokenValue);
            String sub = jwt.getJWTClaimsSet().getSubject();
            if (sub != null && !sub.isBlank()) {
                log.debug("KeycloakId extraído del bearerToken (manual): {}", sub);
                return sub;
            }
        } catch (ParseException e) {
            log.warn("Error al parsear el token JWT manualmente: {}", e.getMessage());
        } catch (Exception e) {
            log.warn("Error al decodificar el bearerToken: {}", e.getMessage());
        }
        return null;
    }

    /**
     * Obtiene el UUID interno del trabajador desde el token JWT.
     * Primero extrae el keycloakId del token, luego consulta el microservicio de workers
     * para obtener el UUID interno del trabajador.
     * 
     * @param bearerToken Token JWT opcional (formato "Bearer <token>" o solo "<token>")
     * @return El UUID interno del trabajador
     * @throws IllegalStateException si no se puede obtener el keycloakId del token
     */
    public UUID getWorkerIdFromToken(String bearerToken) {
        // Obtener keycloakId del token (intenta SecurityContext primero, luego bearerToken)
        String keycloakId = getKeycloakIdFromToken(bearerToken);
        
        if (keycloakId == null || keycloakId.isBlank()) {
            log.warn("No se pudo obtener keycloakId del token");
            throw new IllegalStateException(
                "No se pudo extraer el keycloakId del token JWT. " +
                "Asegúrate de que el usuario esté autenticado correctamente y que el token JWT sea válido."
            );
        }

        log.info("KeycloakId obtenido: {}", keycloakId);
        
        try {
            // Obtener el token completo para pasarlo al cliente
            String tokenForRequest = getTokenValue(bearerToken);
            UUID workerId = workerServiceClient.getWorkerIdByKeycloakId(keycloakId, tokenForRequest);
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
     * Obtiene el token JWT completo.
     * Primero intenta obtenerlo del SecurityContext, si no está disponible,
     * usa el bearerToken proporcionado.
     * 
     * @param bearerToken Token JWT opcional (formato "Bearer <token>" o solo "<token>")
     * @return El token JWT como string, o null si no está disponible
     */
    public String getTokenValue(String bearerToken) {
        // Intentar obtener del SecurityContext primero
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
        
        // Si no está en SecurityContext, usar el bearerToken proporcionado
        if (bearerToken != null && !bearerToken.isBlank()) {
            // Remover el prefijo "Bearer " si existe
            return bearerToken.startsWith(BEARER_PREFIX) 
                ? bearerToken.substring(BEARER_PREFIX.length()) 
                : bearerToken;
        }
        
        return null;
    }
}

