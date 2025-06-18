package org.example.employees.model;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class EmployeeProjectRecord {
  @CsvBindByName(column = "EmpID")
  private Integer employeeId;

  @CsvBindByName(column = "ProjectID")
  private Integer projectId;

  @CsvBindByName(column = "DateFrom")
  private String dateFrom;

  @CsvBindByName(column = "DateTo")
  private String dateTo;
}
