package org.example.employees.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.File;
import java.util.UUID;

import org.example.employees.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.openapitools.model.CommonProjectsResponse;
import org.openapitools.model.CommonProject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Test
    void uploadEmployeesProjects_HappyPath_ShouldReturnAccepted() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "employees.csv",
                MediaType.TEXT_PLAIN_VALUE,
                "employeeId,projectId,daysWorked".getBytes()
        );

        UUID processId = UUID.randomUUID();
        when(employeeService.initializeIdentificationProcess(ArgumentMatchers.any(File.class)))
                .thenReturn(processId);

        // Act & Assert
        mockMvc.perform(multipart("/employees/common-projects")
                        .file(file))
                .andExpect(status().isAccepted())
                .andExpect(header().string("X-Identification-Process-ID", processId.toString()));
    }

    @Test
    void getCommonProjects_HappyPath_ShouldReturnOk() throws Exception {
        // Arrange
        UUID processId = UUID.randomUUID();
        CommonProjectsResponse response = new CommonProjectsResponse()
                .status("SUCCESS")
                .commonProjects(List.of(new CommonProject()
                        .firstEmployeeId(1)
                        .secondEmployeeId(2)
                        .projectId(123)
                        .daysWorked(45)));

        when(employeeService.getCommonProjects(processId)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/employees/common-projects/result/{processId}", processId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.commonProjects[0].firstEmployeeId").value(1))
                .andExpect(jsonPath("$.commonProjects[0].secondEmployeeId").value(2))
                .andExpect(jsonPath("$.commonProjects[0].projectId").value(123))
                .andExpect(jsonPath("$.commonProjects[0].daysWorked").value(45));
    }
}