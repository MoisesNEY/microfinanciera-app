package com.microfinance.customer_microservice.domain.entity;

import com.microfinance.customer_microservice.domain.enums.ContactType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;




@Data
@NoArgsConstructor
@Entity
@Table(name = "contact_info")
public class ContactInfoEntity {

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
    @Column(name = "contact_type", nullable = false)
    private ContactType contactType;

    @Column(name = "contact_value", length = 100, nullable = false)
    private String contactValue;

    @Column(name = "is_primary", nullable = false)
    private boolean isPrimary;
}
