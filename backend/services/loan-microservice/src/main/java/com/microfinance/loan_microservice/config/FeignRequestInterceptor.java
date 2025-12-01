package com.microfinance.loan_microservice.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Interceptor de Feign que añade automáticamente el token JWT
 * del contexto de seguridad a todas las peticiones Feign.
 * 
 * Este interceptor intenta obtener el token JWT de dos formas:
 * 1. Del SecurityContext de Spring Security (método preferido)
 * 2. Del HttpServletRequest actual (fallback)
 */
@Component
public class FeignRequestInterceptor implements RequestInterceptor {

    private static final Logger log = LoggerFactory.getLogger(FeignRequestInterceptor.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public void apply(RequestTemplate template) {
        // Intentar obtener el token del contexto de seguridad de Spring
        String token = getTokenFromSecurityContext();
        
        // Si no está en el contexto de seguridad, intentar obtenerlo del HttpServletRequest
        if (token == null) {
            token = getTokenFromRequest();
        }

        // Si encontramos un token, añadirlo al header
        if (token != null && !token.isBlank()) {
            // Asegurarse de que el token tenga el prefijo "Bearer"
            String bearerToken = token.startsWith(BEARER_PREFIX) ? token : BEARER_PREFIX + token;
            template.header(AUTHORIZATION_HEADER, bearerToken);
            log.debug("Token JWT añadido automáticamente a la petición Feign");
        } else {
            log.warn("No se pudo obtener el token JWT para la petición Feign. La petición puede fallar con 401 Unauthorized.");
        }
    }

    /**
     * Obtiene el token JWT del contexto de seguridad de Spring Security.
     * Este es el método preferido ya que funciona cuando Spring Security
     * ha procesado y validado el token.
     */
    private String getTokenFromSecurityContext() {
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

    /**
     * Obtiene el token JWT del HttpServletRequest actual.
     * Este método funciona como fallback cuando el SecurityContext
     * no está disponible o no contiene el token.
     */
    private String getTokenFromRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String authHeader = request.getHeader(AUTHORIZATION_HEADER);
                if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
                    return authHeader;
                }
            }
        } catch (Exception e) {
            log.debug("No se pudo obtener el token del HttpServletRequest: {}", e.getMessage());
        }
        return null;
    }
}

