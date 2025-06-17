package org.example.employees.service;

import java.util.UUID;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class EmployeeServiceImpl implements EmployeeService {

  public UUID startIdentifyingEmployeesProjects(MultipartFile file) {
    // TODO: Return the real DB record ID
    UUID processId = UUID.randomUUID();

    identifyEmployeesProjects(processId, file);

    return processId;
  }

  // TODO: Specify thrown exceptions
  @Async
  protected void identifyEmployeesProjects(UUID processId, MultipartFile file) {
    // TODO: Process the file and store the results in the DB
  }
}
