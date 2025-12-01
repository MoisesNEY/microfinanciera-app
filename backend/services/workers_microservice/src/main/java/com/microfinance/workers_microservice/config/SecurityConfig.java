package com.microfinance.workers_microservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

        // ============================
        //       MODO DESARROLLO
        // ============================
        if (disabled) {
            return http
                    .authorizeHttpRequests(auth ->
                            auth.anyRequest().permitAll()
                    )
                    .build();
        }

        // ============================
        //     MODO PRODUCCIÓN
        // ============================
        return http
                .authorizeHttpRequests(authz -> authz

                    // -------- Workers (CRUD) --------
                    .requestMatchers(HttpMethod.GET, "/workers/**").permitAll()   // GET público
                    .requestMatchers("/workers/**").authenticated()               // POST/PUT/PATCH/DELETE → requieren token

                    // -------- Departments (para selects dinámicos) --------
                    .requestMatchers(HttpMethod.GET, "/departments/**").permitAll()  // GET público
                    .requestMatchers("/departments/**").authenticated()              // CRUD admin → requiere token

                    // -------- Positions (para selects dinámicos) --------
                    .requestMatchers(HttpMethod.GET, "/positions/**").permitAll()    // GET público
                    .requestMatchers("/positions/**").authenticated()                // CRUD admin → requiere token

                    // -------- Documentación --------
                    .requestMatchers("/api-docs/**").permitAll()
                    .requestMatchers("/swagger-ui/**").permitAll()

                    // -------- Actuator (health checks) --------
                    .requestMatchers("/actuator/**").permitAll()

                    // -------- Cualquier otra ruta --------
                    .anyRequest().authenticated()
                )

                // Keycloak JWT (Resource Server)
                .oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()))
                .build();
    }
}
