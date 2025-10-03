package com.microfinance.customer_microservice.infrastructure.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.microfinance.customer_microservice.domain.entity.AddressEntity;

public interface AddressRepository extends JpaRepository<AddressEntity, UUID> {
}
