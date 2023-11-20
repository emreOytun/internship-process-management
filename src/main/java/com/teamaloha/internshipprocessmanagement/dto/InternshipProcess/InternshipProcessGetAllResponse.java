package com.teamaloha.internshipprocessmanagement.dto.InternshipProcess;

import com.teamaloha.internshipprocessmanagement.entity.InternshipProcess;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InternshipProcessGetAllResponse {
    List<InternshipProcess> internshipProcessList;
}
