package com.teamaloha.internshipprocessmanagement.service;

import com.teamaloha.internshipprocessmanagement.dao.CompanyStaffDao;
import com.teamaloha.internshipprocessmanagement.dto.company.*;
import com.teamaloha.internshipprocessmanagement.dto.companyStaff.*;
import com.teamaloha.internshipprocessmanagement.entity.Company;
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
import java.util.List;
import java.util.stream.Collectors;

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
        CompanyGetResponse isCompanyExists = companyService.get(new CompanyGetRequest(companyAddRequest.getCompanyId()));

        if (isCompanyExists == null) {
            logger.error("Given company id does exists before. CompanyId: " + companyAddRequest.getCompanyId());
            throw new CustomException(ErrorCodeEnum.COMPANY_EXISTS_BEFORE.getErrorCode(), HttpStatus.BAD_REQUEST);
        }
        // Convert given request dto to Company entity.
        CompanyStaff companyStaff = convertDtoToEntity(companyAddRequest);
        companyStaffDao.save(companyStaff);

        Integer id = companyStaff.getId();
        return new CompanyStaffAddResponse(id);
    }

    public CompanyStaffUpdateResponse update(CompanyStaffUpdateRequest companyStaffUpdateRequest) {
        CompanyGetResponse isCompanyExists = companyService.get(new CompanyGetRequest(companyStaffUpdateRequest.getCompanyId()));

        if (isCompanyExists == null) {
            logger.error("Given company not exists before. CompanyId: " + companyStaffUpdateRequest.getCompanyId());
            throw new CustomException(ErrorCodeEnum.COMPANY_NOT_EXISTS_BEFORE.getErrorCode(), HttpStatus.BAD_REQUEST);
        }

        CompanyStaff companyStaff = companyStaffDao.findCompanyStaffById(companyStaffUpdateRequest.getId());
        if(companyStaff == null) {
            logger.error("Given company staff not exists before. Company staff id: " + companyStaffUpdateRequest.getId());
            throw new CustomException(ErrorCodeEnum.COMPANY_STAFF_NOT_EXISTS_BEFORE.getErrorCode(), HttpStatus.BAD_REQUEST);
        }

        BeanUtils.copyProperties(companyStaffUpdateRequest, companyStaff);
        companyStaff.setLogDates(LogDates.builder().createDate(companyStaff.getLogDates().getCreateDate()).updateDate(new Date()).build());
        companyStaffDao.save(companyStaff);

        Integer id = companyStaff.getId();
        return new CompanyStaffUpdateResponse(id);
    }

    public CompanyStaffGetAllResponse getAllByCompanyId(Integer companyId) {
        Company company = new Company();
        company.setId(companyId);
        List<CompanyStaffDto> companyStaffDtoList = companyStaffDao.findCompanyStaffByCompany(company)
                                                    .stream()
                                                    .map(companyStaff -> convertEntityToDto(companyStaff))
                                                    .collect(Collectors.toList());
        return CompanyStaffGetAllResponse.builder().companyStaffList(companyStaffDtoList).build();
    }

    private CompanyStaff convertDtoToEntity(CompanyStaffAddRequest companyStaffAddRequest) {
        CompanyStaff companyStaff = new CompanyStaff();
        Date now = new Date();
        companyStaff.setLogDates(LogDates.builder().createDate(now).updateDate(now).build());
        BeanUtils.copyProperties(companyStaffAddRequest, companyStaff);

        Company company = new Company();
        company.setId(companyStaffAddRequest.getCompanyId());
        companyStaff.setCompany(company);

        return companyStaff;
    }

    private CompanyStaffDto convertEntityToDto(CompanyStaff companyStaff) {
        CompanyStaffDto companyStaffDto = new CompanyStaffDto();
        BeanUtils.copyProperties(companyStaff, companyStaffDto);
        return companyStaffDto;
    }
}
