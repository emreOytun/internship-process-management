package com.teamaloha.internshipprocessmanagement.dto.internshipTypes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InternshipTypesUpdateRequest {
    private int id;
    private String internshipType;
}
