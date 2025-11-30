package com.microfinance.workers_microservice.web;

import com.microfinance.workers_microservice.domain.Department;
import com.microfinance.workers_microservice.service.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/departments")
public class DepartmentController {

    private final DepartmentService service;

    public DepartmentController(DepartmentService service) {
        this.service = service;
    }

    @GetMapping
    public List<Department> list() {
        return service.listActive();
    }

    @GetMapping("/{id}")
    public Department get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    public Department create(@Valid @RequestBody Department request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public Department update(@PathVariable Long id, @Valid @RequestBody Department request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
