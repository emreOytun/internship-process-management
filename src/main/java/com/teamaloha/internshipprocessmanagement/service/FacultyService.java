package com.teamaloha.internshipprocessmanagement.service;

import com.teamaloha.internshipprocessmanagement.dao.FacultyDao;
import com.teamaloha.internshipprocessmanagement.dto.faculty.FacultyAddRequest;
import com.teamaloha.internshipprocessmanagement.dto.faculty.FacultyDto;
import com.teamaloha.internshipprocessmanagement.dto.faculty.FacultyUpdateRequest;
import com.teamaloha.internshipprocessmanagement.dto.faculty.FacultyUpdateResponse;
import com.teamaloha.internshipprocessmanagement.entity.Faculty;
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

@Service
public class FacultyService {

    private final FacultyDao facultyDao;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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

    public Faculty findFacultyByName(String facultyName) {
        return facultyDao.findByFacultyName(facultyName);
    }
    public ResponseEntity<HttpStatus> addFaculty(FacultyAddRequest facultyAddRequest) {
        boolean isFacultyAddedBefore = facultyDao.findByFacultyName(facultyAddRequest.getFacultyName()) != null;
        if (isFacultyAddedBefore) {
            logger.error("Given department exists. Departments: " + facultyAddRequest.getFacultyName());
            throw new CustomException(ErrorCodeEnum.FACULTY_EXIST_BEFORE.getErrorCode(), HttpStatus.BAD_REQUEST);
        }

        // Convert given request dto to Academician entity.
        Faculty faculty = convertDtoToEntity(facultyAddRequest);
        facultyDao.save(faculty);
        // Create token and return it.
        FacultyDto facultyDto = new FacultyDto();
        BeanUtils.copyProperties(faculty, facultyDto);

        return new ResponseEntity(facultyDto, HttpStatus.OK);
    }

    public FacultyUpdateResponse update(FacultyUpdateRequest facultyUpdateRequest) {
        boolean isFacultyExists = facultyDao.existsById(facultyUpdateRequest.getId());

        if (!isFacultyExists) {
            logger.error("Given Faculty not exists before. Faculty: " + facultyUpdateRequest.getFacultyName());
            throw new CustomException(ErrorCodeEnum.Faculty_NOT_EXISTS_BEFORE.getErrorCode(), HttpStatus.BAD_REQUEST);
        }
        Faculty faculty = facultyDao.findFacultyById(facultyUpdateRequest.getId());
        BeanUtils.copyProperties(facultyUpdateRequest, faculty);

        faculty.setLogDates(LogDates.builder().createDate(faculty.getLogDates().getCreateDate()).updateDate(new Date()).build());
        facultyDao.save(faculty);

        Integer id = faculty.getId();
        return new FacultyUpdateResponse(id);
    }
    
    private Faculty convertDtoToEntity(FacultyAddRequest facultyAddRequest) {
        Faculty faculty = new Faculty();
        Date now = new Date();
        faculty.setLogDates(LogDates.builder().createDate(now).updateDate(now).build());
        BeanUtils.copyProperties(facultyAddRequest, faculty);

        return faculty;
    }

}
