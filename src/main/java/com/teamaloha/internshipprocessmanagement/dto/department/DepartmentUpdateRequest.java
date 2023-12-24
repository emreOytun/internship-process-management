package com.teamaloha.internshipprocessmanagement.dto.department;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentUpdateRequest {
    @NotNull
    private Integer id;

    @NotBlank
    private String departmentName;

    @NotBlank
    private String facultyName;
}
