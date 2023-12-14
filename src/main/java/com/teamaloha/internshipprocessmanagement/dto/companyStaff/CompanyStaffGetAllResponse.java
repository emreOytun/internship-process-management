package com.teamaloha.internshipprocessmanagement.dto.companyStaff;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompanyStaffGetAllResponse {
    private List<CompanyStaffDto> companyStaffList;
}
