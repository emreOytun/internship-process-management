package com.teamaloha.internshipprocessmanagement.dto.department;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DepartmentGetAllResponse {
    private List<DepartmentDto> departmentList;
}
