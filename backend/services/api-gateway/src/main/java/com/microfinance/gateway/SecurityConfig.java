package com.microfinance.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {

  @Bean
  SecurityWebFilterChain springSecurityFilterChain(
      ServerHttpSecurity http,
      @Value("${GATEWAY_SECURITY_DISABLED:false}") boolean disabled
  ) {
    http.csrf(ServerHttpSecurity.CsrfSpec::disable);

    if (disabled) {
      // Modo libre (primer arranque sin Keycloak)
      return http
          .authorizeExchange(ex -> ex.anyExchange().permitAll())
          .build();
    }

    // Modo seguro (con Keycloak)
    return http
        .authorizeExchange(ex -> ex
            .pathMatchers("/actuator/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
            .anyExchange().authenticated()
        )
        .oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()))
        .build();
  }
}
