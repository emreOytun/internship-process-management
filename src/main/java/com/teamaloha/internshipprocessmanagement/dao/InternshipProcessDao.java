package com.teamaloha.internshipprocessmanagement.dao;

import com.teamaloha.internshipprocessmanagement.entity.InternshipProcess;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InternshipProcessDao extends JpaRepository<InternshipProcess, Integer> {
    InternshipProcess findByMail(String mail);

    InternshipProcess findInternshipProcessById();
}
