package com.microfinance.customer_microservice.infrastructure.repository;

import com.microfinance.customer_microservice.domain.entity.NationalityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NationalityRepository extends JpaRepository<NationalityEntity, UUID> {
    
    List<NationalityEntity> findByActiveTrue();
    
    Optional<NationalityEntity> findByName(String name);
    
    @Query("SELECT n FROM NationalityEntity n WHERE n.name = 'Otro' AND n.active = true")
    Optional<NationalityEntity> findOtherNationality();
    
    boolean existsByName(String name);
}