package com.teamaloha.internshipprocessmanagement.dao;

import com.teamaloha.internshipprocessmanagement.entity.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacultyDao extends JpaRepository<Faculty, Integer> {
    Faculty findByFacultyName(String facultyName);

    Faculty findFacultyById(Integer id);
}
