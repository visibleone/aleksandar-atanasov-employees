package org.example.employees.service;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.example.employees.model.EmployeeProjectRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class CsvParserServiceImpl implements CsvParserService {
  private static final int CHUNK_SIZE_IN_RECORDS = 1000;

  @Override
  public void parseEmployeeProjectsCsv(
      MultipartFile file, Consumer<List<EmployeeProjectRecord>> chunkConsumer) {
    try (BufferedReader reader =
        new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

      CsvToBean<EmployeeProjectRecord> csvToBean =
          new CsvToBeanBuilder<EmployeeProjectRecord>(reader)
              .withType(EmployeeProjectRecord.class)
              .withIgnoreLeadingWhiteSpace(true)
              .withIgnoreEmptyLine(true)
              .build();

      List<EmployeeProjectRecord> chunk = new ArrayList<>(CHUNK_SIZE_IN_RECORDS);
      for (EmployeeProjectRecord record : csvToBean) {
        chunk.add(record);

        if (chunk.size() >= CHUNK_SIZE_IN_RECORDS) {
          processChunk(chunk);
          chunkConsumer.accept(chunk);
          chunk.clear();
        }
      }

      // Process remaining records
      if (!chunk.isEmpty()) {
        processChunk(chunk);
        chunkConsumer.accept(chunk);
      }

    } catch (Exception e) {
      log.error("Error parsing CSV file", e);
      throw new RuntimeException("Failed to parse CSV file: " + e.getMessage(), e);
    }
  }

  private void processChunk(List<EmployeeProjectRecord> chunk) {
    log.debug("Processing chunk of {} records", chunk.size());
    // TODO: Do processing here
  }
}
