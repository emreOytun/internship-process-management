package com.teamaloha.internshipprocessmanagement.dto.InternshipProcess;

import com.teamaloha.internshipprocessmanagement.entity.InternshipProcess;
import com.teamaloha.internshipprocessmanagement.entity.embeddable.LogDates;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InternshipProcessDto {
    @NotNull
    private Integer id;

    private String tc;

    private String studentNumber;

    private String telephoneNumber;

    private Integer classNumber;

    private String position;

    private String internshipType;

    private Integer internshipNumber;

    private Integer companyId;

    private Integer departmentId;

    @Email
    private String engineerMail;

    private String engineerName;

    private String choiceReason;

    private Boolean sgkEntry;

    private Boolean gssEntry;

    private String mustehaklikBelgesiPath;

    private String stajYeriFormuPath;
}
