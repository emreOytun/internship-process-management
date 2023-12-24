package com.teamaloha.internshipprocessmanagement.dao;

import com.teamaloha.internshipprocessmanagement.entity.InternshipProcess;
import com.teamaloha.internshipprocessmanagement.entity.ProcessAssignee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProcessAssigneeDao extends JpaRepository<ProcessAssignee, Integer> {
    void deleteByInternshipProcess(InternshipProcess process);
    boolean existsByInternshipProcessAndAssigneeId(InternshipProcess process, Integer assigneeId);

    @Query("SELECT pa.internshipProcess.id FROM ProcessAssignee pa WHERE pa.assigneeId = :assigneeId")
    List<Integer> findAllProcessIdByAssigneeId(@Param("assigneeId") Integer assigneeId);
}
