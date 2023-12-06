package com.teamaloha.internshipprocessmanagement.dto.InternshipProcess;

import com.teamaloha.internshipprocessmanagement.dto.SearchByPageDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InternshipProcessSearchDto {
    Date startDate;
    Date endDate;
    String internshipType;
    Integer internshipNumber;
    SearchByPageDto searchByPageDto;
}
