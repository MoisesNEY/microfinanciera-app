package com.microfinance.payment_microservice.config;

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

    @Value("${APP_SECURITY_DISABLED:false}") // true por defecto
    private boolean securityDisabled;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // Desactivar CSRF siempre
        http.csrf(csrf -> csrf.disable())
            .httpBasic(basic -> basic.disable()); // ← esto evita el login básico por defecto

        if (securityDisabled) {
            // Todos los endpoints accesibles sin login
            http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        } else {
            // Seguridad normal (Keycloak / JWT)
            http.authorizeHttpRequests(auth -> auth
                    .requestMatchers("/actuator/**", "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()
                    .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()));
        }

        return http.build();
    }
}
