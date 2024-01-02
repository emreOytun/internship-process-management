package com.teamaloha.internshipprocessmanagement.controller;

import com.teamaloha.internshipprocessmanagement.annotations.CurrentUserId;
import com.teamaloha.internshipprocessmanagement.dto.companyEvaluationSurvey.CompanyEvaluationSurveyAddRequest;
import com.teamaloha.internshipprocessmanagement.dto.companyEvaluationSurvey.CompanyEvaluationSurveyGetResponse;
import com.teamaloha.internshipprocessmanagement.service.CompanyEvaluationSurveyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/company-evaluation-survey")
public class CompanyEvaluationSurveyController {

    private final CompanyEvaluationSurveyService companyEvaluationSurveyService;

    @Autowired
    public CompanyEvaluationSurveyController(CompanyEvaluationSurveyService companyEvaluationSurveyService) {
        this.companyEvaluationSurveyService = companyEvaluationSurveyService;
    }

    @PostMapping("/submit")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).STUDENT.name())")
    public void submitSurvey(@RequestBody @Valid CompanyEvaluationSurveyAddRequest companyEvaluationSurveyAddRequest, @CurrentUserId Integer userId) {
        companyEvaluationSurveyService.submitSurvey(companyEvaluationSurveyAddRequest, userId);
    }

    @GetMapping("/get")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).STUDENT.name())")
    public CompanyEvaluationSurveyGetResponse getSurvey(@RequestParam("internshipProcessId") Integer internshipProcessId, @CurrentUserId Integer userId) {
        return companyEvaluationSurveyService.getSurvey(internshipProcessId, userId);
    }

}
