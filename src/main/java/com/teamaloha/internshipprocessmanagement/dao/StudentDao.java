package com.teamaloha.internshipprocessmanagement.dao;

import com.teamaloha.internshipprocessmanagement.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentDao extends JpaRepository<Student, Integer> {
    Student findByMail(String mail);
    Student findStudentById(Integer ID);

    Student findByPasswordResetToken(String token);
}
