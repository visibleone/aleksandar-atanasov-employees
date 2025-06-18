package org.example.employees.model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;

@Data
public class EmployeeProjectCsvRecord {
  @NotNull
  @CsvBindByName(column = "EmpID", required = true)
  private Integer employeeId;

  @NotNull
  @CsvBindByName(column = "ProjectID", required = true)
  private Integer projectId;

  @NotNull
  @CsvBindByName(column = "DateFrom", required = true)
  @CsvDate("yyyy-MM-dd")
  private LocalDate dateFrom;

  @CsvBindByName(column = "DateTo")
  @CsvDate("yyyy-MM-dd")
  private LocalDate dateTo;
}
