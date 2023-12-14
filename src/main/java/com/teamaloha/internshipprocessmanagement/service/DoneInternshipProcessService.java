package com.teamaloha.internshipprocessmanagement.service;

import com.teamaloha.internshipprocessmanagement.dao.DoneInternshipProcessDao;
import com.teamaloha.internshipprocessmanagement.entity.Company;
import com.teamaloha.internshipprocessmanagement.entity.DoneInternshipProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
public class DoneInternshipProcessService {

    private final DoneInternshipProcessDao doneInternshipProcessDao;
    private final CompanyService companyService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public DoneInternshipProcessService(DoneInternshipProcessDao doneInternshipProcessDao, CompanyService companyService) {
        this.doneInternshipProcessDao = doneInternshipProcessDao;
        this.companyService = companyService;
    }


    Set<Company> findCompainesByDateRange(Date startDate, Date endDate) {
        Set<DoneInternshipProcess> doneInternshipProcesses = doneInternshipProcessDao.findDoneInternshipProcessByStartDateAndEndDate(startDate, endDate);
        Set<Company> companies = new HashSet<>();

        for (DoneInternshipProcess doneInternshipProcess : doneInternshipProcesses) {

            Company company = companyService.findCompanyById(doneInternshipProcess.getCompany().getId());

            if(company != null)
                companies.add(companyService.findCompanyById(doneInternshipProcess.getCompany().getId()));
            else
                logger.error("Given company does not exists before. Company: " + doneInternshipProcess.getCompany().getCompanyName());

        }
        return companies;
    }
}
