package com.teamaloha.internshipprocessmanagement.service;

import com.teamaloha.internshipprocessmanagement.dao.DepartmentDao;
import com.teamaloha.internshipprocessmanagement.entity.Academician;
import com.teamaloha.internshipprocessmanagement.entity.Department;
import com.teamaloha.internshipprocessmanagement.exceptions.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class DepartmentService {

    private final DepartmentDao departmentDao;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    public DepartmentService(DepartmentDao departmentDao) {
        this.departmentDao = departmentDao;
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
}
