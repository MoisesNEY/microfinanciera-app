package com.microfinance.customer_microservice.domain.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.microfinance.customer_microservice.domain.enums.DocumentType;
import com.microfinance.customer_microservice.domain.enums.Gender;
import com.microfinance.customer_microservice.domain.enums.Marital_Status;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.PrePersist;
import jakarta.persistence.GeneratedValue;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "clients")
public class ClientEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "client_code", unique = true, length = 20, nullable = false)
    private String clientCode;

    @Column(name = "first_name", length = 100, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 100, nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "id_document_type", nullable = false)
    private DocumentType idDocumentType;

    @Column(name = "id_document_number", nullable = false, length = 50)
    private String idDocumentNumber;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "gender", nullable = true)
    private Gender gender;

    @Column(name = "nationality", length = 50, nullable = false)
    private String nationality;

    @Enumerated(EnumType.STRING)    
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "marital_status", nullable = true)
    private Marital_Status maritalStatus;

    @Column(name ="occupation", length = 100, nullable = true)
    private String occupation;

    @Column(name ="economic_activity", nullable = false)
    private String economicActivity;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /*
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AddressEntity> addresses = new ArrayList<>();

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContactInfoEntity> contactInfoList = new ArrayList<>();
     */

}
