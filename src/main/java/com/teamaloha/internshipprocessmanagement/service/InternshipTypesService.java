package com.teamaloha.internshipprocessmanagement.service;

import com.teamaloha.internshipprocessmanagement.dao.InternshipTypesDao;
import com.teamaloha.internshipprocessmanagement.dto.holiday.HolidayAddRequest;
import com.teamaloha.internshipprocessmanagement.dto.holiday.HolidayDto;
import com.teamaloha.internshipprocessmanagement.dto.internshipTypes.InternshipTypesAddRequest;
import com.teamaloha.internshipprocessmanagement.dto.internshipTypes.InternshipTypesDto;
import com.teamaloha.internshipprocessmanagement.dto.internshipTypes.InternshipTypesRemoveRequest;
import com.teamaloha.internshipprocessmanagement.dto.internshipTypes.InternshipTypesUpdateRequest;
import com.teamaloha.internshipprocessmanagement.entity.InternshipTypes;
import com.teamaloha.internshipprocessmanagement.entity.Faculty;
import com.teamaloha.internshipprocessmanagement.entity.Holiday;
import com.teamaloha.internshipprocessmanagement.entity.InternshipTypes;
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
public class InternshipTypesService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final InternshipTypesDao internshipTypesDao;

    @Autowired
    public InternshipTypesService(InternshipTypesDao internshipTypesDao) {
        this.internshipTypesDao = internshipTypesDao;
    }
    
    public boolean isInternshipTypeExistsByInternshipType(String internshipType) {
        return internshipTypesDao.existsByInternshipType(internshipType);
    }

    public ResponseEntity<HttpStatus> addInternshipType(InternshipTypesAddRequest internshipTypesAddRequest) {
        boolean isInternshipTypesAddedBefore = isInternshipTypeExistsByInternshipType(internshipTypesAddRequest.getInternshipType());

        if (isInternshipTypesAddedBefore) {
            logger.error("Given intership type exists. Date: " + internshipTypesAddRequest.getInternshipType());
            throw new CustomException(ErrorCodeEnum.INTERNSHIP_EXISTS_BEFORE.getErrorCode(), HttpStatus.BAD_REQUEST);
        }

        // Convert given request dto to Academician entity.
        InternshipTypes internshipTypes = convertDtoToEntity(internshipTypesAddRequest);
        internshipTypesDao.save(internshipTypes);
        // Create token and return it.
        InternshipTypesDto internshipTypesDto = new InternshipTypesDto();
        BeanUtils.copyProperties(internshipTypes, internshipTypesDto);

        return new ResponseEntity(internshipTypesDto, HttpStatus.OK);
    }

    public ResponseEntity<HttpStatus> updateInternshipType(InternshipTypesUpdateRequest internshipTypesUpdateRequest){
        boolean isInternshipTypesExists = internshipTypesDao.existsById(internshipTypesUpdateRequest.getId());

        if (!isInternshipTypesExists) {
            logger.error("Given internshipTypes not exists before. internshipTypes: " + internshipTypesUpdateRequest.getInternshipType());
            throw new CustomException(ErrorCodeEnum.INTERNSHIP_DOES_NOT_EXISTS_BEFORE.getErrorCode(), HttpStatus.BAD_REQUEST);
        }
        InternshipTypes internshipTypes = internshipTypesDao.findById(internshipTypesUpdateRequest.getId());
        internshipTypes.setInternshipType(internshipTypesUpdateRequest.getInternshipType());

        internshipTypes.setLogDates(LogDates.builder().createDate(internshipTypes.getLogDates().getCreateDate()).updateDate(new Date()).build());
        internshipTypesDao.save(internshipTypes);

        return new ResponseEntity(HttpStatus.OK);
    }

    public ResponseEntity<HttpStatus> removeInternshipType(InternshipTypesRemoveRequest internshipTypesRemoveRequest){
        boolean isInternshipTypesExists = internshipTypesDao.existsById(internshipTypesRemoveRequest.getId());

        if (!isInternshipTypesExists) {
            logger.error("Given internshipTypes not exists before. internshipTypes: " + internshipTypesRemoveRequest.getId());
            throw new CustomException(ErrorCodeEnum.INTERNSHIP_DOES_NOT_EXISTS_BEFORE.getErrorCode(), HttpStatus.BAD_REQUEST);
        }
        InternshipTypes internshipTypes = internshipTypesDao.findById(internshipTypesRemoveRequest.getId());
        internshipTypesDao.delete(internshipTypes);

        InternshipTypesDto internshipTypesDto = new InternshipTypesDto();
        BeanUtils.copyProperties(internshipTypes, internshipTypesDto);

        return new ResponseEntity(internshipTypesDto, HttpStatus.OK);
    }

    private InternshipTypes convertDtoToEntity(InternshipTypesAddRequest internshipTypesAddRequest) {
        InternshipTypes internshipTypes = new InternshipTypes();
        Date now = new Date();
        BeanUtils.copyProperties(internshipTypesAddRequest, internshipTypes);
        internshipTypes.setLogDates(LogDates.builder()
                .createDate(now)
                .updateDate(now)
                .build());
        return internshipTypes;
    }
}
