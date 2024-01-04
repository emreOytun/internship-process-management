package com.teamaloha.internshipprocessmanagement.dao;

import com.teamaloha.internshipprocessmanagement.entity.DoneInternshipProcess;
import com.teamaloha.internshipprocessmanagement.entity.InternshipProcess;
import com.teamaloha.internshipprocessmanagement.entity.Student;
import com.teamaloha.internshipprocessmanagement.enums.ProcessStatusEnum;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Set;


public interface DoneInternshipProcessDao extends JpaRepository<DoneInternshipProcess, Integer> {

    Set<DoneInternshipProcess> findDoneInternshipProcessByStartDateAndEndDate(Date startDate, Date endDate);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {
                    "student",
                    "company",
                    "department"
            })
    List<DoneInternshipProcess> findAllByStudent(Student student);

    List<DoneInternshipProcess> findAllByEndDateBetween(Date startDate, Date endDate);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {
                    "student",
                    "company",
                    "department"
            })
    DoneInternshipProcess findDoneInternshipProcessById(Integer processId);

    Integer countByStudentIdAndProcessStatus(Integer studentId, ProcessStatusEnum processStatus);
}
