package com.teamaloha.internshipprocessmanagement.dto.faculty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FacultyUpdateRequest {
    private Integer id;
    private String facultyName;
}
