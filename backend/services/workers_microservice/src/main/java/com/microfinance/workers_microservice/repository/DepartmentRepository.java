package com.microfinance.workers_microservice.repository;

import com.microfinance.workers_microservice.domain.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    Optional<Department> findByCodeIgnoreCase(String code);

    List<Department> findByActiveTrue();
}
