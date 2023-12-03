package com.teamaloha.internshipprocessmanagement.dto.company;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyGetRequest {
    @NotNull
    @NotBlank
    private Integer id;
}