package org.example.employees.repository;

import java.util.UUID;
import org.example.employees.model.entity.EmployeeProjectIdentification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeProjectIdentificationRepository
    extends JpaRepository<EmployeeProjectIdentification, UUID> {}
