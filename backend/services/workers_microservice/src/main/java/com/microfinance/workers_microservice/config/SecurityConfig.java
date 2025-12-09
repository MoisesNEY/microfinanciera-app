package com.microfinance.workers_microservice.config;

import com.microfinance.workers_microservice.config.security.KeycloakJwtAuthenticationConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            KeycloakJwtAuthenticationConverter keycloakJwtConverter,
            @Value("${APP_SECURITY_DISABLED:false}") boolean disabled) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // ============================
        // MODO DESARROLLO
        // ============================
        if (disabled) {
            return http
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                    .build();
        }

        // ============================
        // MODO PRODUCCIÓN
        // ============================
        return http
                .authorizeHttpRequests(authz -> authz
                        // -------- Documentación y Actuator --------
                        .requestMatchers("/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/actuator/**")
                        .permitAll()

                        // -------- Cualquier otra ruta --------
                        .anyRequest().authenticated())

                // Keycloak JWT (Resource Server)
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(keycloakJwtConverter)))
                .build();
    }
}
