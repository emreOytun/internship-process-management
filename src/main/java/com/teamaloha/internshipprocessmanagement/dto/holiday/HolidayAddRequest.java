package com.teamaloha.internshipprocessmanagement.dto.holiday;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HolidayAddRequest {
    @NotNull
    private String date;

    @NotBlank
    private String name;

    @NotBlank
    private String description;
}
