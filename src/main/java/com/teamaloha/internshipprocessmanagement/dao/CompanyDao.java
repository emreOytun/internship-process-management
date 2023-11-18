package com.teamaloha.internshipprocessmanagement.dao;

import com.teamaloha.internshipprocessmanagement.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyDao extends JpaRepository<Company, Integer> {
    Company findCompanyById(Integer id);
    Boolean existsByCompanyName(String companyName);
}
