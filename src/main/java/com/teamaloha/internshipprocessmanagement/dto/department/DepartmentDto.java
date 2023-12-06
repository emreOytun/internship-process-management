package com.teamaloha.internshipprocessmanagement.dto.department;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentDto {
    private int id;
    private String name;
    private String faculty;
}
