package com.microfinance.workers_microservice.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity
@Table(name = "workers",
  uniqueConstraints = {
    @UniqueConstraint(name="uk_workers_document", columnNames="document"),
    @UniqueConstraint(name="uk_workers_email",   columnNames="email")
})
public class Worker {

  @Id @GeneratedValue @UuidGenerator
  private UUID id;

  @NotBlank @Size(max=120)
  @Column(name="first_name", nullable=false, length=120)
  private String firstName;

  @NotBlank @Size(max=120)
  @Column(name="last_name", nullable=false, length=120)
  private String lastName;

  @NotBlank @Size(max=50)
  @Column(nullable=false, length=50)
  private String document;

  @Size(max=20)
  @Column(length=20)
  private String phone;

  @Email @NotBlank @Size(max=150)
  @Column(nullable=false, length=150)
  private String email;

  @NotBlank @Size(max=50)
  @Column(nullable=false, length=50)
  private String position;

  @Size(max=50)
  @Column(length=50)
  private String department;

  @NotNull
  @Column(name="hire_date", nullable=false)
  private LocalDate hireDate;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(nullable=false, length=16)
  private WorkerStatus status;

  @Column(name="created_at", nullable=false, columnDefinition="TIMESTAMP WITH TIME ZONE")
  private OffsetDateTime createdAt;

  @Column(name="updated_at", nullable=false, columnDefinition="TIMESTAMP WITH TIME ZONE")
  private OffsetDateTime updatedAt;
}
