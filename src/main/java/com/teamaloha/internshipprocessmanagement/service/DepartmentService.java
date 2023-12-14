package com.teamaloha.internshipprocessmanagement.service;

import com.teamaloha.internshipprocessmanagement.dao.DepartmentDao;
import com.teamaloha.internshipprocessmanagement.dao.FacultyDao;
import com.teamaloha.internshipprocessmanagement.dto.department.*;
import com.teamaloha.internshipprocessmanagement.dto.holiday.HolidayAddRequest;
import com.teamaloha.internshipprocessmanagement.dto.holiday.HolidayDto;
import com.teamaloha.internshipprocessmanagement.entity.Academician;
import com.teamaloha.internshipprocessmanagement.entity.Department;
import com.teamaloha.internshipprocessmanagement.entity.Faculty;
import com.teamaloha.internshipprocessmanagement.entity.Holiday;
import com.teamaloha.internshipprocessmanagement.entity.embeddable.LogDates;
import com.teamaloha.internshipprocessmanagement.enums.ErrorCodeEnum;
import com.teamaloha.internshipprocessmanagement.exceptions.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartmentService {

    private final DepartmentDao departmentDao;
    private final FacultyService facultyService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public DepartmentService(DepartmentDao departmentDao, FacultyService facultyService){
        this.departmentDao = departmentDao;
        this.facultyService = facultyService;
    }

    // Code to insert mock Department into database.
//    @PostConstruct
//    private void init() {
//        Department department = new Department();
//        Date now = new Date();
//        Faculty faculty = new Faculty();
//        faculty.setId(1);
//
//        department.setDepartmentName("Computer Engineering");
//        department.setFaculty(faculty);
//        department.setLogDates(LogDates.builder().createDate(now).updateDate(now).build());
//        departmentDao.save(department);
//    }

    public Department findDepartmentById(Integer id) {
        return departmentDao.findDepartmentById(id);
    }

    public Department getDepartmentIfExistsOrThrowException(Integer departmentId) {
        Department department = findDepartmentById(departmentId);
        if (department == null) {
            logger.error("The department id given does not exist. Department id: "
                    + departmentId);
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
        return department;
    }

    public DepartmentGetAllResponse getAll() {
        List<DepartmentDto> dtoList =  departmentDao.findAll().stream().map(department -> convertEntityToDto(department)).collect(Collectors.toList());
        return DepartmentGetAllResponse.builder().departmentList(dtoList).build();
    }


    public ResponseEntity<HttpStatus> addDepartment(DepartmentAddRequest departmentAddRequest) {
        boolean isDepartmentAddedBefore = departmentDao.findDepartmentByDepartmentName(departmentAddRequest.getName()) != null;
        Faculty faculty = facultyService.findFacultyByName(departmentAddRequest.getFaculty());

        if (isDepartmentAddedBefore || faculty == null) {
            logger.error("Given department exists. Departments: " + departmentAddRequest.getName());
            throw new CustomException(ErrorCodeEnum.DEPARTMENT_EXIST_BEFORE.getErrorCode(), HttpStatus.BAD_REQUEST);
        }

        // Convert given request dto to Academician entity.
        Department department = convertDtoToEntity(departmentAddRequest, faculty);
        departmentDao.save(department);
        // Create token and return it.
        DepartmentDto departmentDto = new DepartmentDto();
        BeanUtils.copyProperties(department, departmentDto);

        return new ResponseEntity(departmentDto, HttpStatus.OK);
    }

    public DepartmentUpdateResponse update(DepartmentUpdateRequest departmentUpdateRequest) {
        boolean isDepartmentExists = departmentDao.existsById(departmentUpdateRequest.getId());
        Faculty faculty = facultyService.findFacultyByName(departmentUpdateRequest.getFacultyName());

        if (!isDepartmentExists || faculty == null) {
            logger.error("Given Department not exists before. Department: " + departmentUpdateRequest.getDepartmentName());
            throw new CustomException(ErrorCodeEnum.DEPARTMENT_NOT_EXISTS_BEFORE.getErrorCode(), HttpStatus.BAD_REQUEST);
        }
        Department department = departmentDao.findDepartmentById(departmentUpdateRequest.getId());
        department.setDepartmentName(departmentUpdateRequest.getDepartmentName());
        department.setFaculty(faculty);

        department.setLogDates(LogDates.builder().createDate(department.getLogDates().getCreateDate()).updateDate(new Date()).build());
        departmentDao.save(department);

        Integer id = department.getId();
        return new DepartmentUpdateResponse(id);
    }

    private Department convertDtoToEntity(DepartmentAddRequest departmentAddRequest, Faculty faculty) {
        Department department = new Department();
        department.setDepartmentName(departmentAddRequest.getName());
        department.setFaculty(faculty);

        Date now = new Date();
        department.setLogDates(LogDates.builder().createDate(now).updateDate(now).build());

        return department;
    }

    private DepartmentDto convertEntityToDto(Department department) {
        return DepartmentDto.builder().id(department.getId()).faculty(department.getFaculty().getFacultyName())
                .name(department.getDepartmentName()).build();
    }
}
