package org.example.employees.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {
  CsvParserServiceImpl csvParserService;

  public UUID startIdentifyingEmployeesProjects(MultipartFile file) {
    // TODO: Return the real DB record ID
    UUID processId = UUID.randomUUID();

    identifyEmployeesProjectsAsync(processId, file);

    return processId;
  }

  // TODO: Specify thrown exceptions
  @Async
  protected void identifyEmployeesProjectsAsync(UUID processId, MultipartFile file) {
    csvParserService.parseEmployeeProjectsCsv(
        file,
        chunk -> {
          // TODO: Process each chunk here, e.g. save to database
          // employeeRepository.saveAll(chunk);
        });
  }
}
