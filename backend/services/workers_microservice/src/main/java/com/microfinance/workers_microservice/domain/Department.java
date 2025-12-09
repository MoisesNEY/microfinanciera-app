package com.microfinance.workers_microservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Table(name = "departments", uniqueConstraints = {
                @UniqueConstraint(name = "uk_departments_code", columnNames = "code")
})
public class Department {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @NotBlank
        @Size(max = 50)
        @Column(nullable = false, length = 50)
        private String code; // FINANZAS, TI, CREDITOS...

        @NotBlank
        @Size(max = 120)
        @Column(nullable = false, length = 120)
        private String name; // Finanzas, Sistemas / Tecnología, etc.

        @Column(name = "is_active", nullable = false)
        private boolean active = true;
}
