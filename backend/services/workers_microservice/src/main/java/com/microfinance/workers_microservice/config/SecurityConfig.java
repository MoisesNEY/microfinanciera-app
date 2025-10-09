package com.microfinance.workers_microservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            @Value("${APP_SECURITY_DISABLED:false}") boolean disabled
    ) throws Exception {
        http.csrf(csrf -> csrf.disable());

        if (disabled) {
            // Modo desarrollo - sin seguridad (total acceso)
            return http
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                    .build();
        }

        // Modo producción - con seguridad pero endpoints públicos para workers
        return http
                .authorizeHttpRequests(authz -> authz
                    .requestMatchers("/workers/**").permitAll()        // Acceso sin auth a workers
                    .requestMatchers("/api-docs/**").permitAll()    // OpenAPI
                    .requestMatchers("/swagger-ui/**").permitAll()     // Swagger UI
                    .requestMatchers("/actuator/**").permitAll()       // Health checks
                    .anyRequest().authenticated()                      // Resto requiere autenticación
                )
                .oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults())) // Keycloak
                .build();
    }
}