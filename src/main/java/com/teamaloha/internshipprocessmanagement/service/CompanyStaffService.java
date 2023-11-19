package com.teamaloha.internshipprocessmanagement.service;

import com.teamaloha.internshipprocessmanagement.dao.CompanyStaffDao;
import com.teamaloha.internshipprocessmanagement.dto.company.*;
import com.teamaloha.internshipprocessmanagement.dto.companyStaff.CompanyStaffAddRequest;
import com.teamaloha.internshipprocessmanagement.dto.companyStaff.CompanyStaffAddResponse;
import com.teamaloha.internshipprocessmanagement.dto.companyStaff.CompanyStaffUpdateRequest;
import com.teamaloha.internshipprocessmanagement.dto.companyStaff.CompanyStaffUpdateResponse;
import com.teamaloha.internshipprocessmanagement.entity.CompanyStaff;
import com.teamaloha.internshipprocessmanagement.entity.embeddable.LogDates;
import com.teamaloha.internshipprocessmanagement.enums.ErrorCodeEnum;
import com.teamaloha.internshipprocessmanagement.exceptions.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class CompanyStaffService {
    private final CompanyStaffDao companyStaffDao;
    private final CompanyService companyService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public CompanyStaffService(CompanyStaffDao companyStaffDao, CompanyService companyService) {
        this.companyStaffDao = companyStaffDao;
        this.companyService = companyService;
    }


    public CompanyStaffAddResponse add(CompanyStaffAddRequest companyAddRequest) {
        CompanyGetResponse isCompanyExists = companyService.get(new CompanyGetRequest(companyAddRequest.getCompany().getId()));

        if (isCompanyExists == null) {
            logger.error("Given company id does exists before. Company: " + companyAddRequest.getCompany().getCompanyName());
            throw new CustomException(ErrorCodeEnum.COMPANY_EXISTS_BEFORE.getErrorCode(), HttpStatus.BAD_REQUEST);
        }
        // Convert given request dto to Company entity.
        CompanyStaff companyStaff = convertDtoToEntity(companyAddRequest);
        companyStaffDao.save(companyStaff);

        Integer id = companyStaff.getId();
        return new CompanyStaffAddResponse(id);
    }

    public CompanyStaffUpdateResponse update(CompanyStaffUpdateRequest companyStaffUpdateRequest) {
        CompanyGetResponse isCompanyExists = companyService.get(new CompanyGetRequest(companyStaffUpdateRequest.getId()));

        if (isCompanyExists == null) {
            logger.error("Given company not exists before. Company: " + companyStaffUpdateRequest.getCompany().getCompanyName());
            throw new CustomException(ErrorCodeEnum.COMPANY_NOT_EXISTS_BEFORE.getErrorCode(), HttpStatus.BAD_REQUEST);
        }

        CompanyStaff companyStaff = companyStaffDao.findCompanyStaffById(companyStaffUpdateRequest.getId());
        if(companyStaff == null) {
            logger.error("Given company staff not exists before. Company staff id: " + companyStaffUpdateRequest.getId());
            throw new CustomException(ErrorCodeEnum.COMPANY_STAFF_NOT_EXISTS_BEFORE.getErrorCode(), HttpStatus.BAD_REQUEST);
        }

        BeanUtils.copyProperties(companyStaffUpdateRequest, companyStaff);
        companyStaffDao.save(companyStaff);

        Integer id = companyStaff.getId();
        return new CompanyStaffUpdateResponse(id);
    }


    private CompanyStaff convertDtoToEntity(CompanyStaffAddRequest companyStaffAddRequest) {
        CompanyStaff companyStaff = new CompanyStaff();
        Date now = new Date();
        companyStaff.setLogDates(LogDates.builder().createDate(now).updateDate(now).build());
        BeanUtils.copyProperties(companyStaffAddRequest, companyStaff);

        return companyStaff;
    }
}
