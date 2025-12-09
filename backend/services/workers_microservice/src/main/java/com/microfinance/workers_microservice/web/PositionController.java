package com.microfinance.workers_microservice.web;

import com.microfinance.workers_microservice.domain.Position;
import com.microfinance.workers_microservice.dto.PositionUpdateRequest;
import com.microfinance.workers_microservice.service.PositionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/positions")
public class PositionController {

    private final PositionService service;

    public PositionController(PositionService service) {
        this.service = service;
    }

    @PreAuthorize("hasAnyRole('admin_general', 'jefe_rrhh', 'reclutador', 'jefe_admin', 'asistente_admin')")
    @GetMapping("/by-department/{departmentId}")
    public List<Position> listByDepartment(@PathVariable Long departmentId) {
        return service.findByDepartment(departmentId);
    }

    @PreAuthorize("hasAnyRole('admin_general', 'jefe_rrhh', 'reclutador', 'jefe_admin', 'asistente_admin')")
    @GetMapping("/{id}")
    public Position get(@PathVariable Long id) {
        return service.get(id);
    }

    @PreAuthorize("hasAnyRole('admin_general', 'jefe_rrhh')")
    @PostMapping("/{departmentId}")
    public Position create(
            @PathVariable Long departmentId,
            @Valid @RequestBody Position req) {
        return service.create(req, departmentId);
    }

    @PreAuthorize("hasAnyRole('admin_general', 'jefe_rrhh')")
    @PutMapping("/{id}")
    public Position update(@PathVariable Long id, @Valid @RequestBody PositionUpdateRequest req) {
        return service.update(id, req);
    }

    @PreAuthorize("hasAnyRole('admin_general', 'jefe_rrhh')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
