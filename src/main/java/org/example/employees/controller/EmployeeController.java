package org.example.employees.controller;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.employees.service.EmployeeService;
import org.openapitools.api.EmployeesApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class EmployeeController implements EmployeesApi {
  public static final String PROCESS_ID_HEADER = "X-Identification-Process-ID";
  private final EmployeeService employeeService;

  @Override
  // TODO: Validate file
  public ResponseEntity<Void> uploadEmployeesProjects(MultipartFile file) {
    UUID processId = employeeService.startIdentifyingEmployeesProjects(file);

    return ResponseEntity.accepted().header(PROCESS_ID_HEADER, processId.toString()).build();
  }
}
