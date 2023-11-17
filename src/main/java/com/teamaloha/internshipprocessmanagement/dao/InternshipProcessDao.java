package com.teamaloha.internshipprocessmanagement.dao;

import com.teamaloha.internshipprocessmanagement.entity.InternshipProcess;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InternshipProcessDao extends JpaRepository<InternshipProcess, Integer> {

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
}
