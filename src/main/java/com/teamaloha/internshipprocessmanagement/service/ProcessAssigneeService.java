package com.teamaloha.internshipprocessmanagement.service;

import com.teamaloha.internshipprocessmanagement.dao.ProcessAssigneeDao;
import com.teamaloha.internshipprocessmanagement.entity.InternshipProcess;
import com.teamaloha.internshipprocessmanagement.entity.ProcessAssignee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProcessAssigneeService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ProcessAssigneeDao processAssigneeDao;

    @Autowired
    public ProcessAssigneeService(ProcessAssigneeDao processAssigneeDao) {
        this.processAssigneeDao = processAssigneeDao;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveAll(List<ProcessAssignee> processAssigneeList) {
        processAssigneeDao.saveAll(processAssigneeList);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteByProcessId(Integer processId) {
        InternshipProcess process = new InternshipProcess();
        process.setId(processId);
        processAssigneeDao.deleteByInternshipProcess(process);
    }

    boolean existsByProcessIdAndAssigneeId(Integer processId, Integer assigneeId) {
        InternshipProcess process = new InternshipProcess();
        process.setId(processId);
        return processAssigneeDao.existsByInternshipProcessAndAssigneeId(process, assigneeId);
    }

    List<Integer> findAllProcessIdByAssigneeId(Integer assigneeId) {
        return processAssigneeDao.findAllProcessIdByAssigneeId(assigneeId);
    }
}
