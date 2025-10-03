package com.microfinance.workers_microservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springdoc.core.models.GroupedOpenApi;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Workers Microservice API",
        version = "v1",
        description = "CRUD y consulta de trabajadores (ms-workers).",
        contact = @Contact(name = "Microfinance Team", email = "dev@acme.com")
    ),
    servers = {
        @Server(url = "/", description = "Servidor por defecto")
    }
)
public class OpenApiConfig {

    @Bean
    public GroupedOpenApi workersApi() {
        return GroupedOpenApi.builder()
            .group("workers")
            .pathsToMatch("/api/v1/**")
            .packagesToScan("com.microfinance.workers_microservice.web")
            .build();
    }
}
