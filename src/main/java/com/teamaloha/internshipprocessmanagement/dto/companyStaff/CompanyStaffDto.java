package com.teamaloha.internshipprocessmanagement.dto.companyStaff;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyStaffDto {
    private int id;
    private String firstName;
    private String lastName;
    private String mail;
    private String telephone;
    private String address;
    private String position;
    private String department;
    private String company;

}
