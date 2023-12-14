package com.teamaloha.internshipprocessmanagement.dto.academician;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AcademicianGetResponse {
    private String firstName;
    private String lastName;
    private String departmentName;
    private Boolean internshipCommittee;
    private Boolean departmentChair;
    private Boolean executive;
    private Boolean academic;
    private Boolean validated;
}
