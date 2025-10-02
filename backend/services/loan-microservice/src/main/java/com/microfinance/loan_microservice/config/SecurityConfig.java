package com.microfinance.loan_microservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  @Bean
  SecurityFilterChain filterChain(
      HttpSecurity http,
      @Value("${APP_SECURITY_DISABLED:false}") boolean disabled
  ) throws Exception {
    http.csrf(csrf -> csrf.disable());

    if (disabled) {
      // Modo libre (primer arranque sin Keycloak)
      return http
          .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
          .build();
    }

    // Modo seguro (con Keycloak)
    return http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/actuator/**","/v3/api-docs/**","/swagger-ui.html","/swagger-ui/**").permitAll()
            .anyRequest().authenticated()
        )
        .oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()))
        .build();
  }
}
