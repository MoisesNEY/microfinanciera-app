package com.microfinance.workers_microservice.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity
@Table(
    name = "workers",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_workers_document",
            columnNames = { "document_type", "document_number" }
        ),
        @UniqueConstraint(
            name = "uk_workers_email",
            columnNames = "email"
        ),
        @UniqueConstraint(
            name = "uk_workers_keycloak_id",
            columnNames = "keycloak_id"
        )
    }
)
public class Worker {

    @Id
    private UUID id; // 🔹 ID interno del microservicio

    @NotBlank
    @Size(max = 50)
    @Column(name = "keycloak_id", nullable = false, length = 50, unique = true)
    private String keycloakId; // 🔹 ID del usuario en Keycloak

    @NotBlank @Size(max = 120)
    @Column(name = "first_name", nullable = false, length = 120)
    private String firstName;

    @NotBlank @Size(max = 120)
    @Column(name = "last_name", nullable = false, length = 120)
    private String lastName;

    @NotBlank
    @Column(nullable = false, length = 80, unique = true)
    private String username;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false, length = 20)
    private DocumentType documentType;

    @NotBlank @Size(max = 50)
    @Column(name = "document_number", nullable = false, length = 50)
    private String documentNumber;

    @Size(max = 20)
    @Column(length = 20)
    private String phone;

    @Email @NotBlank @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String email;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id", nullable = false)
    private Position position;

    @NotNull
    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private WorkerStatus status;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID(); // 🔥 ahora lo genera el microservicio, no Keycloak
        }
        createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}