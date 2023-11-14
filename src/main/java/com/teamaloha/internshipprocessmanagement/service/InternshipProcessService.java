package com.teamaloha.internshipprocessmanagement.service;

import com.teamaloha.internshipprocessmanagement.dao.InternshipProcessDao;
import com.teamaloha.internshipprocessmanagement.dto.InternshipProcess.InternshipProcessDeleteResponse;
import com.teamaloha.internshipprocessmanagement.dto.InternshipProcess.InternshipProcessDto;
import com.teamaloha.internshipprocessmanagement.dto.InternshipProcess.InternshipProcessInitResponse;
import com.teamaloha.internshipprocessmanagement.dto.InternshipProcess.InternshipProcessUpdateResponse;
import com.teamaloha.internshipprocessmanagement.entity.*;
import com.teamaloha.internshipprocessmanagement.entity.embeddable.LogDates;
import com.teamaloha.internshipprocessmanagement.exceptions.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class InternshipProcessService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final InternshipProcessDao internshipProcessDao;

    private final DepartmentService departmentService;
    private final StudentService studentService;
    private final CompanyService companyService;

    @Autowired
    public InternshipProcessService(InternshipProcessDao internshipProcessDao, DepartmentService departmentService, StudentService studentService, CompanyService companyService) {
        this.internshipProcessDao = internshipProcessDao;
        this.departmentService = departmentService;
        this.studentService = studentService;
        this.companyService = companyService;
    }

    public InternshipProcess saveInternshipProcess(InternshipProcessDto internshipProcessDto) {
        Student student = studentService.findStudentById(internshipProcessDto.getStudentId());
        Department department = departmentService.findDepartmentById(internshipProcessDto.getDepartmentId());
        Company company = companyService.findCompanyById(internshipProcessDto.getCompanyId());

        InternshipProcess internshipProcess =  mapDtoToEntity(internshipProcessDto, student, department, company);
        InternshipProcess savedInternshipProcess = internshipProcessDao.save(internshipProcess);
        logger.info("Created InternshipProcess with ID: " + savedInternshipProcess.getId());
        return (savedInternshipProcess);
    }

    public Optional<InternshipProcess> getInternshipProcessById(Integer id) {
        return internshipProcessDao.findById(id);
    }

    public InternshipProcessDeleteResponse deleteInternshipProcess(Integer id) {
        internshipProcessDao.deleteById(id);
        logger.info("Deleted InternshipProcess with ID: " + id);
        return new InternshipProcessDeleteResponse("Staj başvurusu başarı ile silinmiştir");
    }

    public InternshipProcessUpdateResponse updateInternshipProcess(InternshipProcessDto internshipProcessDto) {
        Integer id = internshipProcessDto.getId();
        boolean processExist = internshipProcessDao.existsById(id);

        if (!processExist)  {
            logger.error("InternshipProcess with ID " + id + " not found for update.");
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
        Student student = studentService.findStudentById(internshipProcessDto.getStudentId());
        /*
        if (student == null) {
            logger.error("Student with given id cannot be found. Student id: "
                    + internshipProcessDto.getStudentId());
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }

         */
        Department department = departmentService.findDepartmentById(internshipProcessDto.getDepartmentId());
        /*
        if (department == null) {
            logger.error("Department with given id cannot be found. Department id: "
                    + internshipProcessDto.getDepartmentId());
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }

         */
        Company company = companyService.findCompanyById(internshipProcessDto.getCompanyId());

        InternshipProcess internshipProcess = mapDtoToEntity(internshipProcessDto, student, department, company);
        internshipProcess.setId(id);
        InternshipProcess updatedInternshipProcess = internshipProcessDao.save(internshipProcess);
        logger.info("Updated InternshipProcess with ID: " + updatedInternshipProcess.getId());
        return new InternshipProcessUpdateResponse("InternshipProcess updated successfully.");



    }

    public InternshipProcessInitResponse initInternshipProcess(String mail) {
        Student student = studentService.findByMail(mail);
        InternshipProcess emptyProcess = new InternshipProcess();
        emptyProcess.setStudent(student); // Staj sürecini öğrenciye bağlayın
        InternshipProcess savedProcess = internshipProcessDao.save(emptyProcess);
        logger.info("Created InternshipProcess with ID: " + savedProcess.getId());
        return new InternshipProcessInitResponse(savedProcess.getId());

    }

    private InternshipProcess mapDtoToEntity(InternshipProcessDto internshipProcessDto, Student student, Department department, Company company) {
        InternshipProcess internshipProcess = new InternshipProcess();
        Date now = new Date();

        BeanUtils.copyProperties(internshipProcessDto, internshipProcess);
        // Map individual fields from DTO to entity
        internshipProcess.setId(internshipProcessDto.getId());
        internshipProcess.setLogDates(LogDates.builder().createDate(now).updateDate(now).build());
        internshipProcess.setStudent(student);
        internshipProcess.setTc(internshipProcessDto.getTc());
        internshipProcess.setStudentNumber(internshipProcessDto.getStudentNumber());
        internshipProcess.setTelephoneNumber(internshipProcessDto.getTelephoneNumber());
        internshipProcess.setClassNumber(internshipProcessDto.getClassNumber());
        internshipProcess.setPosition(internshipProcessDto.getPosition());
        internshipProcess.setInternshipType(internshipProcessDto.getInternshipType());
        internshipProcess.setInternshipNumber(internshipProcessDto.getInternshipNumber());
        internshipProcess.setCompany(company);
        internshipProcess.setDepartment(department);
        internshipProcess.setEngineerMail(internshipProcessDto.getEngineerMail());
        internshipProcess.setEngineerName(internshipProcessDto.getEngineerName());
        internshipProcess.setChoiceReason(internshipProcessDto.getChoiceReason());
        internshipProcess.setSgkEntry(internshipProcessDto.getSgkEntry());
        internshipProcess.setGssEntry(internshipProcessDto.getGssEntry());
        internshipProcess.setAssignerMail(internshipProcessDto.getAssignerMail());
        internshipProcess.setMustehaklikBelgesiPath(internshipProcessDto.getMustehaklikBelgesiPath());
        internshipProcess.setStajYeriFormuPath(internshipProcessDto.getStajYeriFormuPath());

        return internshipProcess;
    }


}
