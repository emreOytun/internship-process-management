package com.teamaloha.internshipprocessmanagement.dto.internshipTypes;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InternshipTypesUpdateRequest {
    @NotNull
    private int id;
    @NotBlank
    private String internshipType;
}
