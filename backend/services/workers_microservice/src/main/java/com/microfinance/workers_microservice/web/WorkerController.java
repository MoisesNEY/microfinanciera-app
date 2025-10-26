package com.microfinance.workers_microservice.web;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.microfinance.workers_microservice.domain.WorkerStatus;
import com.microfinance.workers_microservice.dto.WorkerCreateRequest;
import com.microfinance.workers_microservice.dto.WorkerResponse;
import com.microfinance.workers_microservice.dto.WorkerUpdateRequest;
import com.microfinance.workers_microservice.service.WorkerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/workers")
public class WorkerController {
    private final WorkerService service;
    
    public WorkerController(WorkerService service) { 
        this.service = service; 
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) //  Código 201 para creación
    public WorkerResponse create(@Valid @RequestBody WorkerCreateRequest req) { 
        return service.create(req); 
    }

    @GetMapping
    public Page<WorkerResponse> list(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(name = "status", required = false) WorkerStatus status,
            @RequestParam(name = "position", required = false) String position) {
        return service.list(page, size, sort, status, position);
    }

    @GetMapping("/{id}")
    public WorkerResponse get(@PathVariable UUID id) { 
        return service.get(id); 
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkerResponse> update(@PathVariable UUID id,
                                               @Valid @RequestBody WorkerUpdateRequest req) {
    return ResponseEntity.ok(service.update(id, req));
    }

    // Opcional: PATCH para actualizaciones parciales
    @PatchMapping("/{id}")
    public ResponseEntity<WorkerResponse> patch(@PathVariable UUID id,
                                            @RequestBody JsonNode body) {
    return ResponseEntity.ok(service.patch(id, body));
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) //  Código 204 para eliminación
    public void delete(@PathVariable UUID id) { 
        service.delete(id); 
    }
}
