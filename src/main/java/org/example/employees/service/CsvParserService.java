package org.example.employees.service;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;
import org.example.employees.model.EmployeeProjectCsvRecord;

public interface CsvParserService {
  void parseEmployeeProjectsCsv(File file, Consumer<List<EmployeeProjectCsvRecord>> chunkConsumer);
}
