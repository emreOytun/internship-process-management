package com.teamaloha.internshipprocessmanagement.dto.academician;

import com.teamaloha.internshipprocessmanagement.entity.Academician;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AcademicsGetAllResponse {
    List<Academician> academicsList;
}
