package com.teamaloha.internshipprocessmanagement.dto.doneInternshipProcess;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoneInternshipProcessGetAllResponse {
    List<DoneInternshipProcessGetResponse> internshipProcessList;
}
