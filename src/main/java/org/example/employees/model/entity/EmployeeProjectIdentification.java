package org.example.employees.model.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.example.employees.model.EmployeeProjectRecord;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Getter
@Setter
public class EmployeeProjectIdentification {

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
  private List<EmployeeProjectRecord> result;

  @Column(name = "error_message")
  private String errorMessage;

  @Column(name = "processed_at")
  private Instant processedAt;
}
