package com.teamaloha.internshipprocessmanagement.dao;

import com.teamaloha.internshipprocessmanagement.entity.Company;
import com.teamaloha.internshipprocessmanagement.entity.CompanyStaff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyStaffDao extends JpaRepository<CompanyStaff, Integer> {
    CompanyStaff findCompanyStaffById(Integer id);
    List<CompanyStaff> findCompanyStaffByCompany(Company company);
}
