package org.example.employees.service;

import java.io.File;
import java.util.UUID;

public interface EmployeeService {
  UUID initializeIdentificationProcess(File file);

  void startIdentifyingEmployeesProjectsAsync(UUID processId, File file);
}
