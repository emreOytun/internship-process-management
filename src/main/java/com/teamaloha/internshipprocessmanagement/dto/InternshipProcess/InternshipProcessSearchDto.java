package com.teamaloha.internshipprocessmanagement.dto.InternshipProcess;

import com.teamaloha.internshipprocessmanagement.dto.SearchByPageDto;
import com.teamaloha.internshipprocessmanagement.enums.ProcessStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InternshipProcessSearchDto {
    private Date startDate;
    private Date endDate;
    private List<ProcessStatusEnum> processStatusEnum;
    private String internshipType;
    private Integer internshipNumber;
    private SearchByPageDto searchByPageDto;
}
