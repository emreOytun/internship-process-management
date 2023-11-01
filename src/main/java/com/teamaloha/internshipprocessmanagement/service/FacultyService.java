package com.teamaloha.internshipprocessmanagement.service;

import com.teamaloha.internshipprocessmanagement.dao.FacultyDao;
import com.teamaloha.internshipprocessmanagement.entity.Faculty;
import com.teamaloha.internshipprocessmanagement.entity.embeddable.LogDates;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class FacultyService {

    private final FacultyDao facultyDao;

    @Autowired
    public FacultyService(FacultyDao facultyDao) {
        this.facultyDao = facultyDao;
    }

    // Code to insert a mock faculty to database.
//    @PostConstruct
//    private void init() {
//        Faculty faculty = new Faculty();
//        Date now = new Date();
//        faculty.setFacultyName("Engineering Faculty");
//        faculty.setLogDates(LogDates.builder().createDate(now).updateDate(now).build());
//        facultyDao.save(faculty);
//    }
}
