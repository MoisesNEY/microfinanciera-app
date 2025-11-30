package com.microfinance.workers_microservice.service;

import com.microfinance.workers_microservice.domain.Department;
import com.microfinance.workers_microservice.domain.Position;
import com.microfinance.workers_microservice.repository.DepartmentRepository;
import com.microfinance.workers_microservice.repository.PositionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PositionService {

    private final PositionRepository repo;
    private final DepartmentRepository departmentRepo;

    public PositionService(PositionRepository repo, DepartmentRepository departmentRepo) {
        this.repo = repo;
        this.departmentRepo = departmentRepo;
    }

    @Transactional(readOnly = true)
    public List<Position> findByDepartment(Long departmentId) {
        Department d = departmentRepo.findById(departmentId)
                .orElseThrow(() -> new EntityNotFoundException("Departamento no encontrado"));

        return repo.findByDepartmentAndActiveTrue(d);
    }

    @Transactional(readOnly = true)
    public Position get(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Posición no encontrada"));
    }

    @Transactional
    public Position create(Position p, Long departmentId) {
        Department d = departmentRepo.findById(departmentId)
                .orElseThrow(() -> new EntityNotFoundException("Departamento no encontrado"));

        p.setDepartment(d);
        // clientId viene en el JSON -> p.setClientId(...) ya viene seteado si el frontend lo manda
        return repo.save(p);
    }

    @Transactional
    public Position update(Long id, Position req) {
        Position p = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Posición no encontrada"));

        p.setName(req.getName());
        p.setCode(req.getCode());
        p.setRealmRole(req.getRealmRole());
        p.setClientRole(req.getClientRole());
        p.setClientId(req.getClientId()); // 🔥 NUEVO
        p.setActive(req.isActive());

        return repo.save(p);
    }

    @Transactional
    public void delete(Long id) {
        Position p = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Posición no encontrada"));

        p.setActive(false);
        repo.save(p);
    }
}
