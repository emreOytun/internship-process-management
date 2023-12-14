package com.teamaloha.internshipprocessmanagement.dto.companyStaff;

import com.teamaloha.internshipprocessmanagement.entity.Company;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyStaffAddRequest {
    @NotNull
    @NotBlank
    private String name;

    @NotNull
    @NotBlank
    private String surname;

    @NotNull
    @NotBlank
    private String mail;

    @NotNull
    @NotBlank
    private String telephone;

    @NotNull
    @NotBlank
    private String title;

    @NotNull
    @NotBlank
    private String department;

    @NotNull
    private Integer companyId;
}
