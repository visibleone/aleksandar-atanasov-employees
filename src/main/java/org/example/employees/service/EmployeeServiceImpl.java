package org.example.employees.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.employees.model.EmployeeProjectCsvRecord;
import org.example.employees.model.entity.EmployeeProjectIdentificationEntity;
import org.example.employees.model.entity.ProcessingStatus;
import org.example.employees.repository.EmployeeProjectIdentificationRepository;
import org.openapitools.model.CommonProject;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {
  private final CsvParserService csvParserService;
  private final EmployeeProjectIdentificationRepository employeeRepository;
  private final ObjectMapper objectMapper;

  @Override
  public UUID initializeIdentificationProcess(File file) {
    EmployeeProjectIdentificationEntity entity =
        employeeRepository.save(initializeIdentification(file));

    return entity.getId();
  }

  @Async
  @Transactional
  public void startIdentifyingEmployeesProjectsAsync(UUID processId, File file) {
    try {
      csvParserService.parseEmployeeProjectsCsv(
          file,
          chunk -> {
            List<CommonProject> commonProjects = findCommonProjects(chunk);
            try {
              String jsonResult = objectMapper.writeValueAsString(commonProjects);
              employeeRepository.appendResults(processId, jsonResult);
            } catch (Exception e) {
              log.error("Error serializing common projects", e);
              processFailed(processId, e);
            }
          });

      processCompletedSuccessfully(processId);
    } catch (Exception e) {
      processFailed(processId, e);

      log.error("Error processing CSV file", e);
    }
  }

  private void processFailed(UUID processId, Exception e) {
    employeeRepository
        .findById(processId)
        .ifPresent(
            entity -> {
              entity.setStatus(ProcessingStatus.FAILED);
              entity.setErrorMessage(e.getMessage());
              entity.setProcessedAt(Instant.now());
              employeeRepository.save(entity);
              log.error("Processing failed for processId: {}", processId);
            });
  }

  private void processCompletedSuccessfully(UUID processId) {
    employeeRepository
        .findById(processId)
        .ifPresent(
            entity -> {
              entity.setStatus(ProcessingStatus.SUCCESS);
              entity.setProcessedAt(Instant.now());
              employeeRepository.save(entity);
              log.info("Processing completed for processId: {}", processId);
            });
  }

  private List<CommonProject> findCommonProjects(List<EmployeeProjectCsvRecord> chunk) {
    Map<Integer, List<EmployeeProjectCsvRecord>> employeeProjects = groupProjectsByEmployee(chunk);
    List<CommonProject> result = new ArrayList<>();

    List<Integer> employeeIds = new ArrayList<>(employeeProjects.keySet());
    for (int i = 0; i < employeeIds.size(); i++) {
      for (int j = i + 1; j < employeeIds.size(); j++) {
        findAndAddCommonProjects(employeeIds.get(i), employeeIds.get(j), employeeProjects, result);
      }
    }
    return result;
  }

  private Map<Integer, List<EmployeeProjectCsvRecord>> groupProjectsByEmployee(
      List<EmployeeProjectCsvRecord> chunk) {
    return chunk.stream().collect(Collectors.groupingBy(EmployeeProjectCsvRecord::getEmployeeId));
  }

  private void findAndAddCommonProjects(
      int firstEmployeeId,
      int secondEmployeeId,
      Map<Integer, List<EmployeeProjectCsvRecord>> employeeProjects,
      List<CommonProject> result) {
    List<EmployeeProjectCsvRecord> firstEmployeeAssignments = employeeProjects.get(firstEmployeeId);
    List<EmployeeProjectCsvRecord> secondEmployeeAssignments =
        employeeProjects.get(secondEmployeeId);

    getCommonProjectsWithDuration(firstEmployeeAssignments, secondEmployeeAssignments)
        .forEach(
            (projectId, daysWorked) ->
                result.add(
                    new CommonProject()
                        .firstEmployeeId(firstEmployeeId)
                        .secondEmployeeId(secondEmployeeId)
                        .projectId(projectId)
                        .daysWorked(daysWorked.intValue())));
  }

  private Map<Integer, Long> getCommonProjectsWithDuration(
      List<EmployeeProjectCsvRecord> firstEmployeeAssignments,
      List<EmployeeProjectCsvRecord> secondEmployeeAssignments) {
    return firstEmployeeAssignments.stream()
        .filter(first -> hasMatchingProject(first, secondEmployeeAssignments))
        .map(
            first ->
                calculateProjectOverlap(
                    first, findMatchingRecord(first, secondEmployeeAssignments)))
        .filter(Objects::nonNull)
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  private boolean hasMatchingProject(
      EmployeeProjectCsvRecord record, List<EmployeeProjectCsvRecord> assignments) {
    return assignments.stream()
        .anyMatch(assignment -> assignment.getProjectId().equals(record.getProjectId()));
  }

  private EmployeeProjectCsvRecord findMatchingRecord(
      EmployeeProjectCsvRecord record, List<EmployeeProjectCsvRecord> assignments) {
    return assignments.stream()
        .filter(assignment -> assignment.getProjectId().equals(record.getProjectId()))
        .findFirst()
        .orElseThrow();
  }

  private AbstractMap.SimpleEntry<Integer, Long> calculateProjectOverlap(
      EmployeeProjectCsvRecord first, EmployeeProjectCsvRecord second) {
    LocalDate firstFrom = first.getDateFrom();
    LocalDate firstTo = first.getDateTo() == null ? LocalDate.now() : first.getDateTo();
    LocalDate secondFrom = second.getDateFrom();
    LocalDate secondTo = second.getDateTo() == null ? LocalDate.now() : second.getDateTo();

    if (firstFrom.isBefore(secondTo) && secondFrom.isBefore(firstTo)) {
      LocalDate overlapStart = firstFrom.isAfter(secondFrom) ? firstFrom : secondFrom;
      LocalDate overlapEnd = firstTo.isBefore(secondTo) ? firstTo : secondTo;
      return new AbstractMap.SimpleEntry<>(
          first.getProjectId(), ChronoUnit.DAYS.between(overlapStart, overlapEnd));
    }
    return null;
  }

  private EmployeeProjectIdentificationEntity initializeIdentification(File file) {
    return EmployeeProjectIdentificationEntity.builder()
        .filename(file.getName())
        .uploadTime(java.time.Instant.now())
        .status(ProcessingStatus.PROCESSING)
        .build();
  }
}
