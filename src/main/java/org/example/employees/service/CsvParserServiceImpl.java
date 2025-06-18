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
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CsvParserServiceImpl implements CsvParserService {
  private static final int CHUNK_SIZE_IN_RECORDS = 1000;

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

      List<EmployeeProjectCsvRecord> chunk = new ArrayList<>(CHUNK_SIZE_IN_RECORDS);
      for (EmployeeProjectCsvRecord record : csvToBean) {
        chunk.add(record);

        if (chunk.size() >= CHUNK_SIZE_IN_RECORDS) {
          // processChunk(chunk);
          chunkConsumer.accept(chunk);
          chunk.clear();
        }
      }

      // Process remaining records
      if (!chunk.isEmpty()) {
        // processChunk(chunk);
        chunkConsumer.accept(chunk);
      }

    } catch (Exception e) {
      log.error("Error parsing CSV file", e);
      throw new RuntimeException("Failed to parse CSV file: " + e.getMessage(), e);
    }
  }

  //  private void processChunk(List<EmployeeProjectCsvRecord> chunk) {
  //    log.debug("Processing chunk of {} records", chunk.size());
  //    // TODO: Do processing here
  //  }
}
