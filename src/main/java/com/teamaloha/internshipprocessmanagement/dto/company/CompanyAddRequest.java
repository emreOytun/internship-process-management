package com.teamaloha.internshipprocessmanagement.dto.company;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyAddRequest {
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
