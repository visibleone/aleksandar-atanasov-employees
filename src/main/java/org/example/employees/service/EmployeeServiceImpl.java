package org.example.employees.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.employees.model.EmployeeProjectCsvRecord;
import org.example.employees.model.entity.EmployeeProjectIdentificationEntity;
import org.example.employees.model.entity.ProcessingStatus;
import org.example.employees.repository.EmployeeProjectIdentificationRepository;
import org.openapitools.model.CommonProject;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {
  private final CsvParserService csvParserService;
  private final EmployeeProjectIdentificationRepository employeeRepository;
  private final ObjectMapper objectMapper;

  public UUID startIdentifyingEmployeesProjects(MultipartFile file) {
    EmployeeProjectIdentificationEntity entity =
        employeeRepository.save(initializeIdentification(file));
    identifyEmployeesProjectsAsync(entity.getId(), file);

    return entity.getId();
  }

  // TODO: Specify thrown exceptions
  @Async
  protected void identifyEmployeesProjectsAsync(UUID processId, MultipartFile file) {
    try {
      csvParserService.parseEmployeeProjectsCsv(
          file,
          chunk -> {
            List<CommonProject> commonProjects = findCommonProjects(chunk);
            try {
              String jsonResult = objectMapper.writeValueAsString(commonProjects);
              employeeRepository.appendResult(processId, jsonResult);
            } catch (Exception e) {
              log.error("Error serializing common projects", e);
              throw new RuntimeException("Failed to serialize common projects", e);
            }
          });
    } catch (Exception e) {
      log.error("Error processing CSV file", e);
      throw new RuntimeException("Failed to process CSV file", e);
    }
  }

  private List<CommonProject> findCommonProjects(List<EmployeeProjectCsvRecord> chunk) {
    // TODO: Implement logic to find common projects
    return new ArrayList<>();
  }

  private EmployeeProjectIdentificationEntity initializeIdentification(MultipartFile file) {
    return EmployeeProjectIdentificationEntity.builder()
        .filename(file.getOriginalFilename())
        .uploadTime(java.time.Instant.now())
        .status(ProcessingStatus.PROCESSING)
        .build();
  }
}
