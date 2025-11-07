package com.microfinance.customer_microservice.infrastructure.repository;

import com.microfinance.customer_microservice.domain.entity.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AddressRepository extends JpaRepository<AddressEntity, UUID> {
    
    // Método para encontrar todas las direcciones por ID de cliente
    List<AddressEntity> findByClientId(UUID clientId);
}