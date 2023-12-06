package com.teamaloha.internshipprocessmanagement.dao;

import com.teamaloha.internshipprocessmanagement.entity.InternshipProcess;
import com.teamaloha.internshipprocessmanagement.entity.ProcessAssignee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessAssigneeDao extends JpaRepository<ProcessAssignee, Integer> {
    void deleteByInternshipProcess(InternshipProcess process);
    boolean existsByInternshipProcessAndAssigneeId(InternshipProcess process, Integer assigneeId);
}
