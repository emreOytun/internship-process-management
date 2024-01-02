package com.teamaloha.internshipprocessmanagement.dto.companyEvaluationSurvey;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyEvaluationSurveyAddRequest {
    @NotNull
    private Integer internshipProcessId;

    @NotNull
    private Boolean findOwnselves;

    @NotNull
    private Boolean engineerHelpful;

    @NotNull
    private Boolean expectationsMet;

    @NotNull
    private Boolean salary;

    @NotNull
    private Boolean insurance;

    @NotNull
    private Boolean food;

    @NotNull
    private Boolean transportation;

    @NotNull
    private Boolean shelter;

    @NotNull
    private Boolean recommendation;

    @NotNull
    private Boolean problem;

    @NotBlank
    private String problemDescription;

    @NotNull
    private Boolean network;

    @NotBlank
    private String networkDescription;

    @NotNull
    private Integer professionalPov;

    @NotNull
    private Integer socialPov;

    @NotNull
    private Boolean sharedWithCompany;

}
