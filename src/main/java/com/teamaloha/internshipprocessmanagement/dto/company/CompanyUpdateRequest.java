package com.teamaloha.internshipprocessmanagement.dto.company;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyUpdateRequest {
    @NotNull
    private Integer id;

    @NotBlank
    private String companyName;

    @NotBlank
    private String companyMail;

    @NotBlank
    private String companyTelephone;

    @NotBlank
    private String faxNumber;

    @NotBlank
    private String companyAddress;
}
