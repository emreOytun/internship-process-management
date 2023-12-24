package com.teamaloha.internshipprocessmanagement.service;

import com.teamaloha.internshipprocessmanagement.dao.ProcessOperationDao;
import com.teamaloha.internshipprocessmanagement.entity.ProcessOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProcessOperationService {
    private ProcessOperationDao processOperationDao;

    @Autowired
    public ProcessOperationService(ProcessOperationDao processOperationDao) {
        this.processOperationDao = processOperationDao;
    }

    public void save(ProcessOperation processOperation) {
        processOperationDao.save(processOperation);
    }
}
