package com.teamaloha.internshipprocessmanagement.dto.holiday;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HolidayDto {
    private int id;
    private Date date;
    private String description;
    private String name;
}
