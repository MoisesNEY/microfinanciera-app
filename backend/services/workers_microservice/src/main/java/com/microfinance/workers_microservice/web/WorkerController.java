package com.microfinance.workers_microservice.web;

import com.microfinance.workers_microservice.domain.WorkerStatus;
import com.microfinance.workers_microservice.dto.*;
import com.microfinance.workers_microservice.service.WorkerService;
import com.fasterxml.jackson.databind.JsonNode;
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
