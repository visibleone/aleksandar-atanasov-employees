package org.example.employees.repository;

import java.util.UUID;
import org.example.employees.model.entity.EmployeeProjectIdentificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeProjectIdentificationRepository
    extends JpaRepository<EmployeeProjectIdentificationEntity, UUID> {

  // TODO: Do not use native query
  @Modifying
  @Query(
      value =
          "UPDATE employee_project_identification "
              + "SET result = CASE "
              + "    WHEN result IS NULL THEN CAST(:results AS jsonb) "
              + "    ELSE result || CAST(:results AS jsonb) "
              + "END "
              + "WHERE id = :processId",
      nativeQuery = true)
  void appendResults(@Param("processId") UUID processId, @Param("results") String results);
}
