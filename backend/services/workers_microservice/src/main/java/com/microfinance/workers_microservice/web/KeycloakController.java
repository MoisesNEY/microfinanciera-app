package com.microfinance.workers_microservice.web;

import com.microfinance.workers_microservice.service.KeycloakService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/workers/keycloak")
public class KeycloakController {

    private final KeycloakService keycloakService;

    public KeycloakController(KeycloakService keycloakService) {
        this.keycloakService = keycloakService;
    }

    @PreAuthorize("hasAnyRole('admin_general', 'jefe_rrhh', 'jefe_admin')")
    @GetMapping("/roles/realm")
    public List<Map<String, Object>> listRealmRoles(@RequestHeader(value = "Authorization") String bearerToken) {
        return keycloakService.listRealmRoles(bearerToken);
    }

    @PreAuthorize("hasAnyRole('admin_general', 'jefe_rrhh', 'jefe_admin')")
    @GetMapping("/clients")
    public List<Map<String, Object>> listClients(@RequestHeader(value = "Authorization") String bearerToken) {
        return keycloakService.listClients(bearerToken);
    }

    @PreAuthorize("hasAnyRole('admin_general', 'jefe_rrhh', 'jefe_admin')")
    @GetMapping("/roles/client/{clientId}")
    public List<Map<String, Object>> listClientRoles(
            @RequestHeader(value = "Authorization") String bearerToken,
            @PathVariable String clientId) {
        return keycloakService.listClientRoles(bearerToken, clientId);
    }
}
