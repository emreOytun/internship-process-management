package com.teamaloha.internshipprocessmanagement.dao;

import com.teamaloha.internshipprocessmanagement.entity.Academician;
import com.teamaloha.internshipprocessmanagement.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface HolidayDao extends JpaRepository<Holiday, Integer> {
    boolean existsByDate(String date);
    Holiday save(Holiday holiday);
}
