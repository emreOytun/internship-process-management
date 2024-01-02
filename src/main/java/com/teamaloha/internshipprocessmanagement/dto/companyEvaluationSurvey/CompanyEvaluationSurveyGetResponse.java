package com.teamaloha.internshipprocessmanagement.dto.companyEvaluationSurvey;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyEvaluationSurveyGetResponse {
    private Integer id;
    private Integer internshipProcessId;
    private Boolean findOwnselves;
    private Boolean engineerHelpful;
    private Boolean expectationsMet;
    private Boolean salary;
    private Boolean insurance;
    private Boolean food;
    private Boolean transportation;
    private Boolean shelter;
    private Boolean recommendation;
    private Boolean problem;
    private String problemDescription;
    private Boolean network;
    private String networkDescription;
    private Integer professionalPov;
    private Integer socialPov;
    private Boolean sharedWithCompany;
}
