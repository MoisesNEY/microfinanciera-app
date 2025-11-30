package com.microfinance.workers_microservice.web;

import com.microfinance.workers_microservice.domain.Position;
import com.microfinance.workers_microservice.service.PositionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/positions")
public class PositionController {

    private final PositionService service;

    public PositionController(PositionService service) {
        this.service = service;
    }

    @GetMapping("/by-department/{departmentId}")
    public List<Position> listByDepartment(@PathVariable Long departmentId) {
        return service.findByDepartment(departmentId);
    }

    @GetMapping("/{id}")
    public Position get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping("/{departmentId}")
    public Position create(
            @PathVariable Long departmentId,
            @Valid @RequestBody Position req
    ) {
        return service.create(req, departmentId);
    }

    @PutMapping("/{id}")
    public Position update(@PathVariable Long id, @Valid @RequestBody Position req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
