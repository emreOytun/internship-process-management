package com.teamaloha.internshipprocessmanagement.dto.company;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyDto {
    private int id;
    private String companyName;
    private String companyMail;
    private String companyTelephone;
    private String faxNumber;
    private String companyAddress;

}
