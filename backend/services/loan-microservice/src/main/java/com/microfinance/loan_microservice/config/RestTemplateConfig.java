package com.microfinance.loan_microservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
  @Bean
  RestTemplate restTemplate() {
      SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
      factory.setConnectTimeout(2000);  // 2 segundos para conectar
      factory.setReadTimeout(3000);     // 3 segundos para leer respuesta
      return new RestTemplate(factory);
  }
}
