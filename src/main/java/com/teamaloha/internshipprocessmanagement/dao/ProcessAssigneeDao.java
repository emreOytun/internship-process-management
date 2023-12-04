package com.teamaloha.internshipprocessmanagement.dao;

import com.teamaloha.internshipprocessmanagement.entity.ProcessAssignee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessAssigneeDao extends JpaRepository<ProcessAssignee, Integer> {
    void deleteByProcessId(Integer processId);
    boolean existsByProcessIdAndAssigneeId(Integer processId, Integer assigneeId);
}
