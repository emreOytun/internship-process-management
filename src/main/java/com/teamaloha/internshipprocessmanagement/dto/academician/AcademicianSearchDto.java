package com.teamaloha.internshipprocessmanagement.dto.academician;

import com.teamaloha.internshipprocessmanagement.dto.SearchByPageDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AcademicianSearchDto {
    String name;
    Date createDateStart;
    Date createDateEnd;
    SearchByPageDto searchByPageDto;
}
