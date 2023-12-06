package com.teamaloha.internshipprocessmanagement.dto.holiday;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IsValidRangeRequest {
    Date startDate;
    Date endDate;
    String numberOfWorkingDays;
}
