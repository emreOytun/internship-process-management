package com.teamaloha.internshipprocessmanagement.service;

import com.teamaloha.internshipprocessmanagement.dao.InternshipProcessDao;
import com.teamaloha.internshipprocessmanagement.dto.InternshipProcess.InternshipProcessUpdateRequest;
import com.teamaloha.internshipprocessmanagement.dto.InternshipProcess.InternshipProcessInitResponse;
import com.teamaloha.internshipprocessmanagement.entity.*;
import com.teamaloha.internshipprocessmanagement.entity.embeddable.LogDates;
import com.teamaloha.internshipprocessmanagement.enums.ProcessStatusEnum;
import com.teamaloha.internshipprocessmanagement.exceptions.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class InternshipProcessService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final InternshipProcessDao internshipProcessDao;
    private final DepartmentService departmentService;
    private final CompanyService companyService;

    @Autowired
    public InternshipProcessService(InternshipProcessDao internshipProcessDao, DepartmentService departmentService,
                                    CompanyService companyService) {
        this.internshipProcessDao = internshipProcessDao;
        this.departmentService = departmentService;
        this.companyService = companyService;
    }

    public InternshipProcessInitResponse initInternshipProcess(Integer userId) {
        // Only setting the ID of Student entity is enough to insert InternshipProcess entity.
        Student student = new Student();
        student.setId(userId);

        Date now = new Date();
        InternshipProcess emptyProcess = new InternshipProcess();
        emptyProcess.setStudent(student);
        emptyProcess.setLogDates(LogDates.builder().createDate(now).updateDate(now).build());
        emptyProcess.setProcessStatus(ProcessStatusEnum.FORM);
        InternshipProcess savedProcess = internshipProcessDao.save(emptyProcess);

        logger.info("Created InternshipProcess with ID: " + savedProcess.getId());
        return new InternshipProcessInitResponse(savedProcess.getId());
    }

    public void updateInternshipProcess(InternshipProcessUpdateRequest internshipProcessUpdateRequest, Integer userId) {
        Integer id = internshipProcessUpdateRequest.getId();
        InternshipProcess internshipProcess = internshipProcessDao.findInternshipProcessById(id);

        if (internshipProcess == null) {
            logger.error("InternshipProcess with ID " + id + " not found for update.");
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }

        // Check if the current user id and the student id of the given internship process is matching.
        if (!userId.equals(internshipProcess.getStudent().getId())) {
            logger.error("The internshipProcess id given does not belong to the student. Student id: "
                    + userId);
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }

        // If department id is given, check if there is such department.
        Department department = null;
        if (internshipProcessUpdateRequest.getDepartmentId() != null) {
            department = departmentService.findDepartmentById(internshipProcessUpdateRequest.getDepartmentId());
            if (department == null) {
                logger.error("Department with given id cannot be found. Department id: "
                        + internshipProcessUpdateRequest.getDepartmentId());
                throw new CustomException(HttpStatus.BAD_REQUEST);
            }
        }

        // If company id is given, check if there is such company.
        Company company = null;
        if (internshipProcessUpdateRequest.getCompanyId() != null) {
            company = companyService.findCompanyById(internshipProcessUpdateRequest.getCompanyId());
            if (company == null) {
                logger.error("Company with given id cannot be found. Company id: "
                        + internshipProcessUpdateRequest.getCompanyId());
                throw new CustomException(HttpStatus.BAD_REQUEST);
            }
        }

        copyDtoToEntity(internshipProcess, internshipProcessUpdateRequest, department, company);
        InternshipProcess updatedInternshipProcess = internshipProcessDao.save(internshipProcess);

        logger.info("Updated InternshipProcess with ID: " + updatedInternshipProcess.getId());
    }

    public void deleteInternshipProcess(Integer id) {
        internshipProcessDao.deleteById(id);
        logger.info("Deleted InternshipProcess with ID: " + id);
    }

    private void copyDtoToEntity(InternshipProcess internshipProcess, InternshipProcessUpdateRequest internshipProcessUpdateRequest, Department department, Company company) {
        Date now = new Date();

        BeanUtils.copyProperties(internshipProcessUpdateRequest, internshipProcess);
        internshipProcess.setLogDates(LogDates.builder().createDate(now).updateDate(now).build());
        internshipProcess.setCompany(company);
        internshipProcess.setDepartment(department);
        internshipProcess.setMustehaklikBelgesiPath(internshipProcessUpdateRequest.getMustehaklikBelgesiPath());
        internshipProcess.setStajYeriFormuPath(internshipProcessUpdateRequest.getStajYeriFormuPath());
        internshipProcess.setProcessStatus(ProcessStatusEnum.FORM);
    }
}
