package org.example.employees.model.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "employee_project_identification")
public class EmployeeProjectIdentificationEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String filename;

  @Column(name = "upload_time", nullable = false)
  private Instant uploadTime;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ProcessingStatus status;

  @Column(name = "result", columnDefinition = "jsonb")
  @JdbcTypeCode(SqlTypes.JSON)
  private String result;

  @Column(name = "error_message")
  private String errorMessage;

  @Column(name = "processed_at")
  private Instant processedAt;
}
