package com.teamaloha.internshipprocessmanagement.dto.companyStaff;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyStaffUpdateRequest {
    @NotNull
    private Integer id;

    @NotBlank
    private String name;

    @NotBlank
    private String surname;

    @NotBlank
    private String mail;

    @NotBlank
    private String telephone;

    @NotBlank
    private String title;

    @NotBlank
    private String department;

    @NotNull
    private Integer companyId;
}
