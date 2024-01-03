package com.teamaloha.internshipprocessmanagement.dao;

import com.teamaloha.internshipprocessmanagement.entity.InternshipProcess;
import com.teamaloha.internshipprocessmanagement.entity.Student;
import com.teamaloha.internshipprocessmanagement.enums.ProcessStatusEnum;
import org.springframework.data.jpa.repository.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface InternshipProcessDao extends JpaRepository<InternshipProcess, Integer>,
                                                JpaSpecificationExecutor<InternshipProcess> {

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {
                    "student",
                    "company",
                    "department"
            })
    InternshipProcess findInternshipProcessById(Integer id);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {
                    "student",
                    "company",
                    "department"
            })
    List<InternshipProcess> findAll();

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {
                    "student",
                    "company",
                    "department"
            })
    List<InternshipProcess> findAllByStudent(Student student);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {
                    "student",
                    "company",
                    "department"
            })
    List<InternshipProcess> findAllByStudentAndProcessStatusNot(Student student, ProcessStatusEnum processStatus);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {
                    "student",
                    "company",
                    "department"
            })
    List<InternshipProcess> findAllByProcessStatus(ProcessStatusEnum processStatus);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {
                    "student",
                    "company",
                    "department"
            })
    List<InternshipProcess> findAllByProcessStatusIn(List<ProcessStatusEnum> processStatuses);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {
                    "student",
                    "company",
                    "department"
            })
    List<InternshipProcess>findAllByCompany_Id(Integer companyId);

    Integer countByStudentId(Integer studentId);
    List<InternshipProcess> findAllByAssignerId(Integer assignerId);
    // If there are special Specifications needed for Internship process add here.
//    interface Specs { }

    @Query("UPDATE InternshipProcess i SET i.rejected = false WHERE i.rejected IS NULL")
    @Modifying
    @Transactional
    void updateNullRejectedFields();
}
