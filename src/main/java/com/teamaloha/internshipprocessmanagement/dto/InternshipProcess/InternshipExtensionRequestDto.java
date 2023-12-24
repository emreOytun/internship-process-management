package com.teamaloha.internshipprocessmanagement.dto.InternshipProcess;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InternshipExtensionRequestDto {
    @NotNull
    private Integer processId;
    @NotNull
    private Date requestDate;
    @NotBlank
    private String extensionDayNumber;

}
