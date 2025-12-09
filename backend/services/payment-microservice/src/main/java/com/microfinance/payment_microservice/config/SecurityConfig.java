package com.microfinance.payment_microservice.config;

import com.microfinance.payment_microservice.config.security.KeycloakJwtAuthenticationConverter;
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

    @Value("${APP_SECURITY_DISABLED:false}") // false por defecto
    private boolean securityDisabled;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, KeycloakJwtAuthenticationConverter keycloakJwtConverter)
            throws Exception {
        // Desactivar CSRF, basic auth y forzar stateless para REST
        http.csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        if (securityDisabled) {
            // Todos los endpoints accesibles sin login
            http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        } else {
            // Seguridad normal (Keycloak / JWT)
            http.authorizeHttpRequests(auth -> auth
                    .requestMatchers("/actuator/**", "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**")
                    .permitAll()
                    .anyRequest().authenticated())
                    .oauth2ResourceServer(oauth -> oauth
                            .jwt(jwt -> jwt.jwtAuthenticationConverter(keycloakJwtConverter)));
        }

        return http.build();
    }
}
