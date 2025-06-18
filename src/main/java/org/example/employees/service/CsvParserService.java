package org.example.employees.service;

import java.util.List;
import java.util.function.Consumer;
import org.example.employees.model.EmployeeProjectCsvRecord;
import org.springframework.web.multipart.MultipartFile;

public interface CsvParserService {
  void parseEmployeeProjectsCsv(
      MultipartFile file, Consumer<List<EmployeeProjectCsvRecord>> chunkConsumer);
}
