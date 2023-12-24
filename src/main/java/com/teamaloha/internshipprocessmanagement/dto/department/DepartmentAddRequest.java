package com.teamaloha.internshipprocessmanagement.dto.department;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentAddRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String faculty;
}
