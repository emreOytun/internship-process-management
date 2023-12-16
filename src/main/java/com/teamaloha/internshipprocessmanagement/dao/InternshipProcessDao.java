package com.teamaloha.internshipprocessmanagement.dao;

import com.teamaloha.internshipprocessmanagement.entity.InternshipProcess;
import com.teamaloha.internshipprocessmanagement.entity.Student;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

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

    Integer countByStudentId(Integer studentId);
    List<InternshipProcess> findAllByAssignerId(Integer assignerId);
    // If there are special Specifications needed for Internship process add here.
//    interface Specs { }
}
