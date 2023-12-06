package com.teamaloha.internshipprocessmanagement.service;

import com.teamaloha.internshipprocessmanagement.dao.HolidayDao;
import com.teamaloha.internshipprocessmanagement.dto.holiday.HolidayAddRequest;
import com.teamaloha.internshipprocessmanagement.dto.holiday.HolidayDto;
import com.teamaloha.internshipprocessmanagement.dto.holiday.IsValidRangeRequest;
import com.teamaloha.internshipprocessmanagement.entity.Holiday;
import com.teamaloha.internshipprocessmanagement.enums.ErrorCodeEnum;
import com.teamaloha.internshipprocessmanagement.exceptions.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class HolidayService {
    private final HolidayDao holidayDao;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public HolidayService(HolidayDao holidayDao) {
        this.holidayDao = holidayDao;
    }

    public ResponseEntity<HttpStatus> addHoliday(HolidayAddRequest holidayAddRequest) {
        boolean isHolidayAddedBefore = isHolidayExistsByDate(holidayAddRequest.getDate());
        if (isHolidayAddedBefore) {
            logger.error("Given date exists. Date: " + holidayAddRequest.getDate());
            throw new CustomException(ErrorCodeEnum.MAIL_EXISTS_BEFORE.getErrorCode(), HttpStatus.BAD_REQUEST);
        }

        // Convert given request dto to Academician entity.
        Holiday holiday = convertDtoToEntity(holidayAddRequest);
        holidayDao.save(holiday);
        // Create token and return it.
        HolidayDto holidayDto = new HolidayDto();
        BeanUtils.copyProperties(holiday, holidayDto);

        return new ResponseEntity(holidayDto, HttpStatus.OK);
    }
    public boolean isHolidayExistsByDate(String date) {
        return holidayDao.existsByDate(date);
    }

    private Holiday convertDtoToEntity(HolidayAddRequest holidayAddRequest) {
        Holiday holiday = new Holiday();
        Date now = new Date();
        BeanUtils.copyProperties(holidayAddRequest, holiday);

        return holiday;
    }

    public boolean isGivenWorkDayTrue(IsValidRangeRequest isValidRangeRequest) {
        int numberOfDays = Integer.parseInt(isValidRangeRequest.getNumberOfWorkingDays());
        Date startDate = isValidRangeRequest.getStartDate();
        Date endDate = isValidRangeRequest.getEndDate();


        while (startDate.compareTo(endDate) <= 0) {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String formattedStartDate = dateFormat.format(startDate);

            if (!(holidayDao.existsByDate(formattedStartDate) || startDate.getDay() == 0 || startDate.getDay() == 6  ) )
                numberOfDays--;

            startDate.setDate(startDate.getDate() + 1);
        }

        return numberOfDays == 0;
    }
}
