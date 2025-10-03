package com.microfinance.workers_microservice.web;

import com.microfinance.workers_microservice.domain.WorkerStatus;
import com.microfinance.workers_microservice.dto.*;
import com.microfinance.workers_microservice.service.WorkerService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/workers")
public class WorkerController {
  private final WorkerService service;
  public WorkerController(WorkerService service){ this.service = service; }

  @PostMapping public WorkerResponse create(@Valid @RequestBody WorkerCreateRequest req){ return service.create(req); }

  @GetMapping
  public Page<WorkerResponse> list(@RequestParam(defaultValue="0") Integer page,
                                   @RequestParam(defaultValue="10") Integer size,
                                   @RequestParam(required=false) String sort,
                                   @RequestParam(required=false) WorkerStatus status,
                                   @RequestParam(required=false) String position){
    return service.list(page, size, sort, status, position);
  }

  @GetMapping("{id}") public WorkerResponse get(@PathVariable UUID id){ return service.get(id); }
  @PutMapping("{id}") public WorkerResponse update(@PathVariable UUID id, @Valid @RequestBody WorkerUpdateRequest req){ return service.update(id, req); }
  @DeleteMapping("{id}") public void delete(@PathVariable UUID id){ service.delete(id); }
}
