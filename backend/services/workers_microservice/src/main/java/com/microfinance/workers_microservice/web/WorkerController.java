package com.microfinance.workers_microservice.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.microfinance.workers_microservice.domain.WorkerStatus;
import com.microfinance.workers_microservice.dto.*;
import com.microfinance.workers_microservice.service.WorkerService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/workers")
public class WorkerController {

    private final WorkerService service;

    public WorkerController(WorkerService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // Código 201 para creación
    public WorkerResponse create(
            @Valid @RequestBody WorkerCreateRequest req,
            @RequestHeader("Authorization") String bearerToken
    ) {
        return service.create(req, bearerToken);
    }

    @GetMapping
    public Page<WorkerResponse> list(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(name = "status", required = false) WorkerStatus status
    ) {
        return service.list(page, size, sort, status);
    }

    @GetMapping("/{id}")
    public WorkerResponse get(
            @PathVariable UUID id,
            @RequestParam(required = false) WorkerStatus status
    ) {
        return service.get(id, status);
    }

    @GetMapping("/by-keycloak-id/{keycloakId}")
    public WorkerResponse getByKeycloakId(@PathVariable String keycloakId) {
        return service.getByKeycloakId(keycloakId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkerResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody WorkerUpdateRequest req,
            @RequestHeader("Authorization") String bearerToken
    ) {
        return ResponseEntity.ok(service.update(id, req, bearerToken));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<WorkerResponse> patch(
            @PathVariable UUID id,
            @RequestBody JsonNode body,
            @RequestHeader("Authorization") String bearerToken
    ) {
        return ResponseEntity.ok(service.patch(id, body, bearerToken));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Código 204 para eliminación
    public void delete(
            @PathVariable UUID id,
            @RequestHeader("Authorization") String bearerToken
    ) {
        service.delete(id, bearerToken);
    }
}
