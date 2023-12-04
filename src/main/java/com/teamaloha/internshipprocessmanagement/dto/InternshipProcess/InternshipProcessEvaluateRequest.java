package com.teamaloha.internshipprocessmanagement.dto.InternshipProcess;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InternshipProcessEvaluateRequest {
    @NotNull
    private Integer processId;

    @NotNull
    private Boolean approve;

    private String comment;
}
