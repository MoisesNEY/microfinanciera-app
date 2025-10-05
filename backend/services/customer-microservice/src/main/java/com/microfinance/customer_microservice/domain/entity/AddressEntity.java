package com.microfinance.customer_microservice.domain.entity;

import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import com.microfinance.customer_microservice.domain.enums.AddressType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "addresses")
public class AddressEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private ClientEntity client;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "address_type", nullable = false)
    private AddressType addressType;

    @Column(name = "department", length = 100, nullable = false)
    private String department;

    @Column(name = "municipality", length = 100, nullable = false)
    private String municipality;

    @Column(name = "street_address", length = 255, nullable = false, columnDefinition = "TEXT")
    private String streetAddress;

    @Column(name = "is_primary", nullable = false)
    private boolean isPrimary;
}
