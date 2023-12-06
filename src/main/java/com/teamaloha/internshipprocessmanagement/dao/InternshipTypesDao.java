package com.teamaloha.internshipprocessmanagement.dao;

import com.teamaloha.internshipprocessmanagement.entity.InternshipTypes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InternshipTypesDao extends JpaRepository<InternshipTypes, Integer> {
    boolean existsByInternshipType(String internshipType);

    InternshipTypes findById(int id);
}
