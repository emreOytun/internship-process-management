package com.teamaloha.internshipprocessmanagement.dto.companyStaff;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyStaffDto {
    private int id;
    private String name;
    private String surname;
    private String mail;
    private String telephone;
    private String title;
    private String department;
}
