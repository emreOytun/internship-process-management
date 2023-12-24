package com.teamaloha.internshipprocessmanagement.dao;

import com.teamaloha.internshipprocessmanagement.entity.ProcessOperation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessOperationDao extends JpaRepository<ProcessOperation, Integer> {
}
