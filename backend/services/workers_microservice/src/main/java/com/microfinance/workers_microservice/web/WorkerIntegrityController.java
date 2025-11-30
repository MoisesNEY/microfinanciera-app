package com.microfinance.workers_microservice.web;

import com.microfinance.workers_microservice.dto.*;
import com.microfinance.workers_microservice.service.WorkerIntegrityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/workers")
public class WorkerIntegrityController {

    private final WorkerIntegrityService integrityService;

    public WorkerIntegrityController(WorkerIntegrityService integrityService) {
        this.integrityService = integrityService;
    }

    // =========================
    //   REPORTE COMPLETO
    // =========================
    @GetMapping("/integrity-check")
    public IntegrityReportResponse integrityCheck(
            @RequestHeader("Authorization") String bearerToken
    ) {
        return integrityService.runFullIntegrityCheck(bearerToken);
    }

    // =========================
    //   LISTAS PARCIALES
    // =========================
    @GetMapping("/keycloak/unlinked")
    public List<KeycloakUserWithoutWorkerResponse> listKeycloakUnlinked(
            @RequestHeader("Authorization") String bearerToken
    ) {
        return integrityService.keycloakUsersWithoutWorkers(bearerToken);
    }

    @GetMapping("/no-keycloak")
    public List<WorkerWithoutKeycloakResponse> listWorkersWithoutKeycloak(
            @RequestHeader("Authorization") String bearerToken
    ) {
        return integrityService.workersWithoutKeycloakUsers(bearerToken);
    }

    @GetMapping("/roles/inconsistent")
    public List<InconsistentRoleResponse> listInconsistentRoles() {
        return integrityService.inconsistentRolesOnly();
    }

    // =========================
    //   ACCIONES
    // =========================

    // Convertir usuario Keycloak "huérfano" en Worker
    @PostMapping("/from-keycloak/{keycloakUserId}")
    @ResponseStatus(HttpStatus.CREATED)
    public WorkerResponse createFromKeycloak(
            @PathVariable String keycloakUserId,
            @Valid @RequestBody ConvertFromKeycloakRequest body,
            @RequestHeader("Authorization") String bearerToken
    ) {
        return integrityService.createWorkerFromKeycloakUser(bearerToken, keycloakUserId, body);
    }

    // Recrear usuario Keycloak para un Worker existente
    @PostMapping("/{workerId}/recreate-keycloak-user")
    public WorkerResponse recreateKeycloakUser(
            @PathVariable UUID workerId,
            @RequestHeader("Authorization") String bearerToken
    ) {
        return integrityService.recreateKeycloakUser(workerId, bearerToken);
    }

    // Resincronizar roles (local + Keycloak) según la Position actual
    @PostMapping("/{workerId}/resync-roles")
    public WorkerResponse resyncRoles(
            @PathVariable UUID workerId,
            @RequestHeader("Authorization") String bearerToken
    ) {
        return integrityService.resyncRoles(workerId, bearerToken);
    }
}
