package com.microfinance.customer_microservice.infrastructure.repository;

import com.microfinance.customer_microservice.domain.entity.OccupationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OccupationRepository extends JpaRepository<OccupationEntity, UUID> {
    
    List<OccupationEntity> findByActiveTrue();
    
    Optional<OccupationEntity> findByName(String name);
    
    @Query("SELECT o FROM OccupationEntity o WHERE o.name = 'Otro' AND o.active = true")
    Optional<OccupationEntity> findOtherOccupation();
    
    boolean existsByName(String name);
}