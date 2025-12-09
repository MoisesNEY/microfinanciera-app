package com.microfinance.workers_microservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Table(name = "positions", uniqueConstraints = {
                @UniqueConstraint(name = "uk_positions_department_code", columnNames = { "department_id", "code" })
})
public class Position {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        // A1: cada posición pertenece a un departamento
        @NotNull
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "department_id", nullable = false)
        private Department department;

        @NotBlank
        @Size(max = 50)
        @Column(nullable = false, length = 50)
        private String code; // CONTADOR, ASESOR_CREDITO, CAJERO...

        @NotBlank
        @Size(max = 120)
        @Column(nullable = false, length = 120)
        private String name; // Contador, Asesor de créditos, Cajero, etc.

        // Mapeo con roles de Keycloak (automático según la posición)
        @Size(max = 80)
        @Column(name = "realm_role", length = 80)
        private String realmRole; // p.ej. "contador", "asesor_credito"

        @Size(max = 80)
        @Column(name = "client_role", length = 80)
        private String clientRole; // p.ej. "ROLE_USER", "ROLE_ADMIN"

        // 🔥 NUEVO: cliente donde vive el client_role
        @Size(max = 200)
        @Column(name = "client_id", length = 200)
        private String clientId; // p.ej. "frontend-client"

        @Column(name = "is_active", nullable = false)
        private boolean active = true;
}
