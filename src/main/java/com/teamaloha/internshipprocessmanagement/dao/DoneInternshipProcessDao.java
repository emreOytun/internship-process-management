package com.teamaloha.internshipprocessmanagement.dao;

import com.teamaloha.internshipprocessmanagement.entity.DoneInternshipProcess;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Set;


public interface DoneInternshipProcessDao extends JpaRepository<DoneInternshipProcess, Integer>{

    Set<DoneInternshipProcess> findDoneInternshipProcessByStartDateAndEndDate(Date startDate, Date endDate);
}
