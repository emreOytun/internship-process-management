package com.teamaloha.internshipprocessmanagement.service;

import com.teamaloha.internshipprocessmanagement.dao.ProcessAssigneeDao;
import com.teamaloha.internshipprocessmanagement.entity.ProcessAssignee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProcessAssigneeService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ProcessAssigneeDao processAssigneeDao;

    @Autowired
    public ProcessAssigneeService(ProcessAssigneeDao processAssigneeDao) {
        this.processAssigneeDao = processAssigneeDao;
    }

    public void saveAll(List<ProcessAssignee> processAssigneeList) {
        processAssigneeDao.saveAll(processAssigneeList);
    }
}
