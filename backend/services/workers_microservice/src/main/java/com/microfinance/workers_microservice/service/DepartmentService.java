package com.microfinance.workers_microservice.service;

import com.microfinance.workers_microservice.domain.Department;
import com.microfinance.workers_microservice.repository.DepartmentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DepartmentService {

    private final DepartmentRepository repo;

    public DepartmentService(DepartmentRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<Department> listActive() {
        return repo.findByActiveTrue();
    }

    @Transactional(readOnly = true)
    public Department get(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Departamento no encontrado"));
    }

    @Transactional
    public Department create(Department d) {
        return repo.save(d);
    }

    @Transactional
    public Department update(Long id, Department request) {
        Department d = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Departamento no encontrado"));

        d.setName(request.getName());
        d.setCode(request.getCode());
        d.setActive(request.isActive());

        return repo.save(d);
    }

    @Transactional
    public void delete(Long id) {
        Department d = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Departamento no encontrado"));

        d.setActive(false);
        repo.save(d);
    }
}
