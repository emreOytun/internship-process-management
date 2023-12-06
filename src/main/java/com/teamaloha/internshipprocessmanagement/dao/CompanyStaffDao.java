package com.teamaloha.internshipprocessmanagement.dao;

import com.teamaloha.internshipprocessmanagement.entity.CompanyStaff;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyStaffDao extends JpaRepository<CompanyStaff, Integer> {
    CompanyStaff findCompanyStaffById(Integer id);
}
