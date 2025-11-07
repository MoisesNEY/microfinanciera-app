package com.microfinance.customer_microservice.infrastructure.repository;

import com.microfinance.customer_microservice.domain.entity.ContactInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ContactInfoRepository extends JpaRepository<ContactInfoEntity, UUID> {
    
    // Método para encontrar todos los contactos por ID de cliente
    List<ContactInfoEntity> findByClientId(UUID clientId);
}