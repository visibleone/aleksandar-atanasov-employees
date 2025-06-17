package org.example.employees.service;

import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface EmployeeService {
  // TODO: Specify thrown exceptions
  UUID startIdentifyingEmployeesProjects(MultipartFile file);
}
