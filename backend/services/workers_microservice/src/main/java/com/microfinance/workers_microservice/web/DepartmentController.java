package com.microfinance.workers_microservice.web;

import com.microfinance.workers_microservice.domain.Department;
import com.microfinance.workers_microservice.service.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/departments")
public class DepartmentController {

    private final DepartmentService service;

    public DepartmentController(DepartmentService service) {
        this.service = service;
    }

    @PreAuthorize("hasAnyRole('admin_general', 'jefe_rrhh', 'reclutador', 'jefe_admin', 'asistente_admin')")
    @GetMapping
    public List<Department> list() {
        return service.listActive();
    }

    @PreAuthorize("hasAnyRole('admin_general', 'jefe_rrhh', 'reclutador', 'jefe_admin', 'asistente_admin')")
    @GetMapping("/{id}")
    public Department get(@PathVariable Long id) {
        return service.get(id);
    }

    @PreAuthorize("hasAnyRole('admin_general', 'jefe_rrhh')")
    @PostMapping
    public Department create(@Valid @RequestBody Department request) {
        return service.create(request);
    }

    @PreAuthorize("hasAnyRole('admin_general', 'jefe_rrhh')")
    @PutMapping("/{id}")
    public Department update(@PathVariable Long id, @Valid @RequestBody Department request) {
        return service.update(id, request);
    }

    @PreAuthorize("hasAnyRole('admin_general', 'jefe_rrhh')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
