package com.teamaloha.internshipprocessmanagement.dto.authentication;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AcademicianRegisterRequest extends BaseRegisterRequest {
    @NotNull
    Integer departmentId;

}
