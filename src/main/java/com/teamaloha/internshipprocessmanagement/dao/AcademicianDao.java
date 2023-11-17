package com.teamaloha.internshipprocessmanagement.dao;

import com.teamaloha.internshipprocessmanagement.entity.Academician;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AcademicianDao extends JpaRepository<Academician, Integer> {

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH,
    attributePaths = {
            "department"
    })
    Academician findByMail(String mail);
}
