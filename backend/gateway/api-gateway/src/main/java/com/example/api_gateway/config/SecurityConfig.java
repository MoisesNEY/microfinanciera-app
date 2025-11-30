package com.example.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)

            // 🔥 Activa CORS con configuración personalizada
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            .authorizeExchange(exchanges -> exchanges
                // ======== ENDPOINTS PÚBLICOS ========
                .pathMatchers("/departments/**").permitAll()
                .pathMatchers("/positions/**").permitAll()

                // ======== ACTUATOR ========
                .pathMatchers("/actuator/**").permitAll()

                // ======== CORS/OPTIONS ========
                .pathMatchers(HttpMethod.OPTIONS).permitAll()

                // ======== ENDPOINTS PROTEGIDOS ========
                .pathMatchers("/workers/**").authenticated()

                // ======== RESTO REQUIERE TOKEN ========
                .anyExchange().authenticated()
            )

            // JWT con Keycloak
            .oauth2ResourceServer(ServerHttpSecurity.OAuth2ResourceServerSpec::jwt);

        return http.build();
    }

    // =======================================================
    // 🔥 CONFIGURACIÓN DE CORS 100% COMPATIBLE CON WebFlux
    // =======================================================
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",
                "http://127.0.0.1:5173",
                "http://mf_frontend_dev:5173",
                "http://keycloak:8085"
        ));

        config.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));

        config.setAllowedHeaders(Arrays.asList(
                "*" // 🔥 Aceptamos todo, simplifica el preflight
        ));

        config.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Disposition"
        ));

        config.setAllowCredentials(true);

        // Aplica CORS a todos los endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
