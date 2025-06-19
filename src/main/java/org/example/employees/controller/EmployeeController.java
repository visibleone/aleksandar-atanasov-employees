package org.example.employees.controller;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.employees.service.EmployeeService;
import org.openapitools.api.EmployeesApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Slf4j
public class EmployeeController implements EmployeesApi {
  public static final String PROCESS_ID_HEADER = "X-Identification-Process-ID";
  private static final String TEMP_FILE_PREFIX = "upload-";

  private final EmployeeService employeeService;

  @Override
  public ResponseEntity<Void> uploadEmployeesProjects(MultipartFile file) {
    try {
      if (file.isEmpty()) {
        return ResponseEntity.badRequest().build();
      }

      File tempFile = createTempFile(file);
      UUID processId = employeeService.initializeIdentificationProcess(tempFile);
      employeeService.startIdentifyingEmployeesProjectsAsync(processId, tempFile);

      return ResponseEntity.accepted().header(PROCESS_ID_HEADER, processId.toString()).build();
    } catch (RuntimeException e) {
      log.error("Error processing upload request", e);
      return ResponseEntity.badRequest().build();
    }
  }

  private File createTempFile(MultipartFile file) {
    try {
      File tempFile = File.createTempFile(TEMP_FILE_PREFIX, file.getOriginalFilename());
      file.transferTo(tempFile);
      return tempFile;
    } catch (IOException e) {
      log.error("Error creating temporary file", e);
      throw new RuntimeException("Failed to process uploaded file", e);
    }
  }
}
