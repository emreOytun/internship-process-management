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

    // For report edit request
    private Boolean reportEditRequest;

    // Given day number to student for edit report
    private Integer reportEditDays;

    private String comment;

    @NotNull
    private Integer academicianId;
}
