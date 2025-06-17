package org.example.employees.controller;

import org.openapitools.api.EmployeesApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class EmployeeController implements EmployeesApi {
  @Override
  public ResponseEntity<Void> uploadEmployeesProjects(MultipartFile file) {
    return null;
  }
}
