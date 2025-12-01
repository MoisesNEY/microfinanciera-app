package com.microfinance.workers_microservice.domain;

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
@Table(
        name = "worker_roles",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_worker_roles_worker_role",
                        columnNames = { "worker_id", "role_name" }
                )
        }
)
public class WorkerRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id", nullable = false)
    private Worker worker;

    @NotBlank
    @Size(max = 80)
    @Column(name = "role_name", nullable = false, length = 80)
    private String roleName; // Igualito al nombre del rol en Keycloak
}
