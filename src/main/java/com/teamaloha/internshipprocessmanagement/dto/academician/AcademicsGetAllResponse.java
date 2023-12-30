package com.teamaloha.internshipprocessmanagement.dto.academician;

import com.teamaloha.internshipprocessmanagement.entity.Academician;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AcademicsGetAllResponse {
    private List<AcademicianGetResponse> academicsList;
}
