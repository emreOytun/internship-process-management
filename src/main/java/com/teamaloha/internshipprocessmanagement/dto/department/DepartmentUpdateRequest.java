package com.teamaloha.internshipprocessmanagement.dto.department;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentUpdateRequest {
    private Integer id;
    private String departmentName;
    private String facultyName;
}
