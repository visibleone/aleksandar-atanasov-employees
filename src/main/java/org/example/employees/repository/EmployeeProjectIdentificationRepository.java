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
  @Modifying
  @Query(
      "UPDATE EmployeeProjectIdentificationEntity e SET e.result = CONCAT(COALESCE(e.result, ''), :result) WHERE e.id = :processId")
  void appendResult(@Param("processId") UUID processId, @Param("result") String result);

  //  // TODO: Do not use native query
  //  @Modifying
  //  @Transactional
  //  @Query(value = """
  //    UPDATE employee_project_identification
  //    SET result = COALESCE(result, '[]'::jsonb) || CAST(:newRecords AS jsonb)
  //    WHERE id = :id
  //    """, nativeQuery = true)
  //  int appendResults(@Param("processId") UUID processId, @Param("newRecords") String
  // newRecordsJson);
}
