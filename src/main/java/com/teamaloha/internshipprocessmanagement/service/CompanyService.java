package com.teamaloha.internshipprocessmanagement.service;

import com.teamaloha.internshipprocessmanagement.dao.CompanyDao;
import com.teamaloha.internshipprocessmanagement.dto.company.*;
import com.teamaloha.internshipprocessmanagement.entity.Company;
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
public class CompanyService {
    private final CompanyDao companyDao;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public CompanyService(CompanyDao companyDao) {
        this.companyDao = companyDao;
    }

    public Company findCompanyById(Integer id) {
        return companyDao.findCompanyById(id);
    }

    public CompanyAddResponse add(CompanyAddRequest companyAddRequest) {
        boolean isCompanyExists = companyDao.existsByCompanyName(companyAddRequest.getCompanyName());

        if (isCompanyExists) {
            logger.error("Given company exists before. Company: " + companyAddRequest.getCompanyName());
            throw new CustomException(ErrorCodeEnum.COMPANY_EXISTS_BEFORE.getErrorCode(), HttpStatus.BAD_REQUEST);
        }

        // Convert given request dto to Company entity.
        Company company = convertDtoToEntity(companyAddRequest);
        companyDao.save(company);

        Integer id = company.getId();
        return new CompanyAddResponse(id);
    }

    public CompanyUpdateResponse update(CompanyUpdateRequest companyUpdateRequest) {
        boolean isCompanyExists = companyDao.existsById(companyUpdateRequest.getId());

        if (!isCompanyExists) {
            logger.error("Given company not exists before. Company: " + companyUpdateRequest.getCompanyName());
            throw new CustomException(ErrorCodeEnum.COMPANY_NOT_EXISTS_BEFORE.getErrorCode(), HttpStatus.BAD_REQUEST);
        }
        Company company = companyDao.findCompanyById(companyUpdateRequest.getId());
        BeanUtils.copyProperties(companyUpdateRequest, company);

        company.setLogDates(LogDates.builder().createDate(company.getLogDates().getCreateDate()).updateDate(new Date()).build());
        companyDao.save(company);

        Integer id = company.getId();
        return new CompanyUpdateResponse(id);
    }

    public CompanyGetResponse get(CompanyGetRequest companyGetRequest) {
        boolean isCompanyExists = companyDao.existsById(companyGetRequest.getId());

        if (!isCompanyExists) {
            logger.error("Given company not exists before. Company: " + companyGetRequest.getId());
            throw new CustomException(ErrorCodeEnum.COMPANY_NOT_EXISTS_BEFORE.getErrorCode(), HttpStatus.BAD_REQUEST);
        }
        Company company = companyDao.findCompanyById(companyGetRequest.getId());
        CompanyGetResponse companyGetResponse = new CompanyGetResponse();
        BeanUtils.copyProperties(company, companyGetResponse);

        return companyGetResponse;
    }

    public CompanyGetAllResponse getAll() {
        List<CompanyGetResponse> companyGetResponses = companyDao.findAll().stream().map(company -> convertEntityToDto(company)).collect(Collectors.toList());
        return CompanyGetAllResponse.builder().companyList(companyGetResponses).build();
    }

    private Company convertDtoToEntity(CompanyAddRequest companyAddRequest) {
        Company company = new Company();
        Date now = new Date();
        company.setLogDates(LogDates.builder().createDate(now).updateDate(now).build());
        BeanUtils.copyProperties(companyAddRequest, company);

        return company;
    }

    private CompanyGetResponse convertEntityToDto(Company company) {
        return CompanyGetResponse.builder()
                .id(company.getId())
                .companyName(company.getCompanyName())
                .companyMail(company.getCompanyMail())
                .companyTelephone(company.getCompanyTelephone())
                .faxNumber(company.getFaxNumber())
                .companyAddress(company.getCompanyAddress())
                .build();
    }
}
