package com.teamaloha.internshipprocessmanagement.dto.faculty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FacultyUpdateRequest {
    @NotNull
    private Integer id;

    @NotBlank
    private String facultyName;
}
