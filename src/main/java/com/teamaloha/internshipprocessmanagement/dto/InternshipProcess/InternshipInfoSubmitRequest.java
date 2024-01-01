package com.teamaloha.internshipprocessmanagement.dto.InternshipProcess;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InternshipInfoSubmitRequest {
    @NotNull
    private Integer id;

    @NotBlank
    private String position;

    @NotBlank
    private String engineerName;

    @Email
    private String engineerMail;
}
