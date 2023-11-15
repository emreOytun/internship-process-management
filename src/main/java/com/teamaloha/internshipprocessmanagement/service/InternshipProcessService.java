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

 //  public InternshipProcess saveInternshipProcess(InternshipProcessDto internshipProcessDto) {
 //      Student student = studentService.findStudentById(internshipProcessDto.getStudentId());
 //      Department department = departmentService.findDepartmentById(internshipProcessDto.getDepartmentId());
 //      Company company = companyService.findCompanyById(internshipProcessDto.getCompanyId());

 //      InternshipProcess internshipProcess =  mapDtoToEntity(internshipProcessDto, student, department, company);
 //      InternshipProcess savedInternshipProcess = internshipProcessDao.save(internshipProcess);
 //      logger.info("Created InternshipProcess with ID: " + savedInternshipProcess.getId());
 //      return (savedInternshipProcess);
 //  }

    public InternshipProcessInitResponse initInternshipProcess(Integer userId) {
        Student student = studentService.findStudentById(userId);
        InternshipProcess emptyProcess = new InternshipProcess();
        Date now = new Date();
        emptyProcess.setStudent(student);
        emptyProcess.setLogDates(LogDates.builder().createDate(now).updateDate(now).build());
        InternshipProcess savedProcess = internshipProcessDao.save(emptyProcess);

        logger.info("Created InternshipProcess with ID: " + savedProcess.getId());
        return new InternshipProcessInitResponse(savedProcess.getId());
    }

    public InternshipProcessUpdateResponse updateInternshipProcess(InternshipProcessDto internshipProcessDto, Integer userId) {
        Integer id = internshipProcessDto.getId();
        InternshipProcess internshipProcess = internshipProcessDao.findInternshipProcessById(id);

        if (internshipProcess == null) {
            logger.error("InternshipProcess with ID " + id + " not found for update.");
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }

        // TODO: Student-process uyusuyorm u kontrol et
        Student student = studentService.findStudentById(userId);

        if (student.getId().equals(internshipProcess.getStudent().getId())) {
            logger.error("The internshipProcess id given does not belong to the student. Student id: "
                    + student.getId());
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }

        Department department = departmentService.findDepartmentById(internshipProcessDto.getDepartmentId());

        if (department == null) {
            logger.error("Department with given id cannot be found. Department id: "
                    + internshipProcessDto.getDepartmentId());
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }


        Company company = companyService.findCompanyById(internshipProcessDto.getCompanyId());

        moveDtoToEntity(internshipProcess, internshipProcessDto, student, department, company);
        InternshipProcess updatedInternshipProcess = internshipProcessDao.save(internshipProcess);

        logger.info("Updated InternshipProcess with ID: " + updatedInternshipProcess.getId());
        return new InternshipProcessUpdateResponse("InternshipProcess updated successfully.");
    }

    public InternshipProcessDeleteResponse deleteInternshipProcess(Integer id) {
        internshipProcessDao.deleteById(id);

        logger.info("Deleted InternshipProcess with ID: " + id);
        return new InternshipProcessDeleteResponse("Staj başvurusu başarı ile silinmiştir");
    }

    // TODO: Gereksizleri sil. Ve yeni olusturma entity den gelene kopyala
    private void moveDtoToEntity(InternshipProcess internshipProcess, InternshipProcessDto internshipProcessDto, Student student, Department department, Company company) {

        Date now = new Date();

        BeanUtils.copyProperties(internshipProcessDto, internshipProcess);
        // Map individual fields from DTO to entity
        internshipProcess.setLogDates(LogDates.builder().createDate(now).updateDate(now).build());
        internshipProcess.setStudent(student);
        internshipProcess.setCompany(company);
        internshipProcess.setDepartment(department);
        //internshipProcess.setMustehaklikBelgesiPath(internshipProcessDto.getMustehaklikBelgesiPath());
        //internshipProcess.setStajYeriFormuPath(internshipProcessDto.getStajYeriFormuPath());

    }
}
