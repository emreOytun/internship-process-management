package com.teamaloha.internshipprocessmanagement.service;


import com.teamaloha.internshipprocessmanagement.dao.CompanyEvaluationSurveyDao;
import com.teamaloha.internshipprocessmanagement.dto.companyEvaluationSurvey.CompanyEvaluationSurveyAddRequest;
import com.teamaloha.internshipprocessmanagement.dto.companyEvaluationSurvey.CompanyEvaluationSurveyGetResponse;
import com.teamaloha.internshipprocessmanagement.entity.CompanyEvaluationSurvey;
import com.teamaloha.internshipprocessmanagement.entity.InternshipProcess;
import com.teamaloha.internshipprocessmanagement.entity.embeddable.LogDates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class CompanyEvaluationSurveyService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final CompanyEvaluationSurveyDao companyEvaluationSurveyDao;

    private final InternshipProcessService internshipProcessService;

    public CompanyEvaluationSurveyService(CompanyEvaluationSurveyDao companyEvaluationSurveyDao, InternshipProcessService internshipProcessService) {
        this.companyEvaluationSurveyDao = companyEvaluationSurveyDao;
        this.internshipProcessService = internshipProcessService;
    }


    public void submitSurvey(CompanyEvaluationSurveyAddRequest companyEvaluationSurveyAddRequest, Integer studentId) {
        // Check if process is exist
        InternshipProcess internshipProcess = internshipProcessService.getInternshipProcessIfExistsOrThrowException(
                companyEvaluationSurveyAddRequest.getInternshipProcessId());

        // Check if student and internship process is matched
        internshipProcessService.checkIfStudentIdAndInternshipProcessMatchesOrThrowException(studentId, internshipProcess.getStudent().getId());

        // Check if survey is already submitted
        checkIfSurveyIsAlreadySubmittedOrThrowException(internshipProcess);

        // Create survey
        CompanyEvaluationSurvey companyEvaluationSurvey = new CompanyEvaluationSurvey();
        convertDtoToEntity(companyEvaluationSurveyAddRequest, companyEvaluationSurvey, internshipProcess);
        companyEvaluationSurvey.setInternshipProcess(internshipProcess);

        // Save survey
        companyEvaluationSurveyDao.save(companyEvaluationSurvey);

        logger.info("Survey is submitted for internship process with id: {}", internshipProcess.getId());
    }

    public CompanyEvaluationSurveyGetResponse getSurvey(Integer internshipProcessId, Integer studentId) {
        // Check if process is exist
        InternshipProcess internshipProcess = internshipProcessService.getInternshipProcessIfExistsOrThrowException(
                internshipProcessId);

        // Check if student and internship process is matched
        internshipProcessService.checkIfStudentIdAndInternshipProcessMatchesOrThrowException(studentId,
                internshipProcess.getStudent().getId());

        // Get survey If exist or throw exception
        CompanyEvaluationSurvey companyEvaluationSurvey = getCompanyEvaluationSurveyIfExistsOrThrowException(internshipProcess);

        // Convert entity to dto
        CompanyEvaluationSurveyGetResponse companyEvaluationSurveyGetResponse = new CompanyEvaluationSurveyGetResponse();
        convertEntityToDto(companyEvaluationSurvey, companyEvaluationSurveyGetResponse);

        return companyEvaluationSurveyGetResponse;
    }

    private void convertDtoToEntity(CompanyEvaluationSurveyAddRequest companyEvaluationSurveyAddRequest,
                                    CompanyEvaluationSurvey companyEvaluationSurvey, InternshipProcess internshipProcess) {
        Date now = new Date();

        BeanUtils.copyProperties(companyEvaluationSurveyAddRequest, companyEvaluationSurvey);
        companyEvaluationSurvey.setInternshipProcess(internshipProcess);
        companyEvaluationSurvey.setLogDates(LogDates.builder().createDate(now).updateDate(now).build());
    }

    private void convertEntityToDto(CompanyEvaluationSurvey companyEvaluationSurvey,
                                    CompanyEvaluationSurveyGetResponse companyEvaluationSurveyGetResponse) {
        BeanUtils.copyProperties(companyEvaluationSurvey, companyEvaluationSurveyGetResponse);

        companyEvaluationSurveyGetResponse.setInternshipProcessId(companyEvaluationSurvey.getInternshipProcess().getId());
    }

    private void checkIfSurveyIsAlreadySubmittedOrThrowException(InternshipProcess internshipProcess) {
        if (companyEvaluationSurveyDao.countAllByInternshipProcess(internshipProcess) > 0) {
            logger.error("Survey is already submitted for internship process id: {}", internshipProcess.getId());
            throw new RuntimeException("Survey is already submitted for internship process id: " + internshipProcess.getId());
        }
    }

    private CompanyEvaluationSurvey getCompanyEvaluationSurveyIfExistsOrThrowException(InternshipProcess internshipProcess) {
        CompanyEvaluationSurvey companyEvaluationSurvey = companyEvaluationSurveyDao.findByInternshipProcess(internshipProcess);

        if (companyEvaluationSurvey == null) {
            logger.error("Survey is not submitted for internship process id: {}", internshipProcess.getId());
            throw new RuntimeException("Survey is not submitted for internship process id: " + internshipProcess.getId());
        }

        return companyEvaluationSurvey;
    }




}