package com.teamaloha.internshipprocessmanagement.dto.academician;

import com.teamaloha.internshipprocessmanagement.dto.InternshipProcess.InternshipProcessGetResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AcademicsGetStudentAllProcessResponse {
    List<InternshipProcessGetResponse> internshipProcessList;
}
