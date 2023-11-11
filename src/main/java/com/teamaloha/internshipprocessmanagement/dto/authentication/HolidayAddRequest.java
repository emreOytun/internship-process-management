package com.teamaloha.internshipprocessmanagement.dto.authentication;

import lombok.Data;

import java.util.Date;

@Data
public class HolidayAddRequest {
    String date;
    String name;
    String description;
}
