package com.microfinance.loan_microservice.config;

import com.microfinance.loan_microservice.config.security.KeycloakJwtAuthenticationConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

  @Bean
  SecurityFilterChain filterChain(
      HttpSecurity http,
      KeycloakJwtAuthenticationConverter keycloakJwtConverter,
      @Value("${APP_SECURITY_DISABLED:false}") boolean disabled) throws Exception {
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
            .requestMatchers("/actuator/**", "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()
            .anyRequest().authenticated())
        .oauth2ResourceServer(oauth -> oauth
            .jwt(jwt -> jwt.jwtAuthenticationConverter(keycloakJwtConverter)))
        .build();
  }

  @Bean
  public JwtDecoder jwtDecoder(@Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String issuerUri) {
    return new LazyJwtDecoder(issuerUri);
  }

  private static class LazyJwtDecoder implements JwtDecoder {
    private final String issuerUri;
    private volatile JwtDecoder delegate;

    public LazyJwtDecoder(String issuerUri) {
      this.issuerUri = issuerUri;
    }

    @Override
    public Jwt decode(String token) throws JwtException {
      if (delegate == null) {
        synchronized (this) {
          if (delegate == null) {
            delegate = JwtDecoders.fromIssuerLocation(issuerUri);
          }
        }
      }
      return delegate.decode(token);
    }
  }
}
