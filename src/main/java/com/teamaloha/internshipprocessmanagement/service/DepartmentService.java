package com.teamaloha.internshipprocessmanagement.service;

import com.teamaloha.internshipprocessmanagement.dao.DepartmentDao;
import com.teamaloha.internshipprocessmanagement.entity.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class DepartmentService {

    private final DepartmentDao departmentDao;

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
}
