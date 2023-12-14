package com.teamaloha.internshipprocessmanagement.dto.companyStaff;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyStaffGetAllByCompanyRequest {
    @NotNull
    private Integer companyId;
}
