package com.microfinance.workers_microservice.repository;

import com.microfinance.workers_microservice.domain.Department;
import com.microfinance.workers_microservice.domain.Position;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PositionRepository extends JpaRepository<Position, Long> {

    List<Position> findByDepartmentAndActiveTrue(Department department);

    Optional<Position> findByDepartmentAndCodeIgnoreCase(Department department, String code);

    Optional<Position> findByRealmRole(String realmRole);
}