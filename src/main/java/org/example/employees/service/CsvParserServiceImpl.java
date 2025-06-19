package org.example.employees.service;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.example.employees.model.EmployeeProjectCsvRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CsvParserServiceImpl implements CsvParserService {
  @Value("${employees.csv.chunk-size-in-records}")
  private int chunkSizeInRecords;

  @Override
  public void parseEmployeeProjectsCsv(
      File file, Consumer<List<EmployeeProjectCsvRecord>> chunkConsumer) {
    try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {

      CsvToBean<EmployeeProjectCsvRecord> csvToBean =
          new CsvToBeanBuilder<EmployeeProjectCsvRecord>(reader)
              .withType(EmployeeProjectCsvRecord.class)
              .withIgnoreLeadingWhiteSpace(true)
              .withIgnoreEmptyLine(true)
              .withThrowExceptions(true)
              .build();

      List<EmployeeProjectCsvRecord> chunk = new ArrayList<>(chunkSizeInRecords);
      for (EmployeeProjectCsvRecord record : csvToBean) {
        chunk.add(record);

        if (chunk.size() >= chunkSizeInRecords) {
          chunkConsumer.accept(chunk);
          chunk.clear();
        }
      }

      // Process remaining records
      if (!chunk.isEmpty()) {
        chunkConsumer.accept(chunk);
      }

    } catch (Exception e) {
      log.error("Error parsing CSV file", e);
      throw new RuntimeException("Failed to parse CSV file: " + e.getMessage(), e);
    }
  }
}
