package com.teamaloha.internshipprocessmanagement.service;


import com.teamaloha.internshipprocessmanagement.dao.StorageDao;
import com.teamaloha.internshipprocessmanagement.entity.InternshipProcess;
import com.teamaloha.internshipprocessmanagement.entity.PDFData;
import com.teamaloha.internshipprocessmanagement.entity.embeddable.LogDates;
import com.teamaloha.internshipprocessmanagement.utilities.PDFUtils;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

@Service
public class StorageService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private StorageDao storageDao;
    private InternshipProcessService internshipProcessService;
    @Autowired
    public StorageService(StorageDao storageDao, InternshipProcessService internshipProcessService) {
        this.storageDao = storageDao;
        this.internshipProcessService = internshipProcessService;
    }

    @Transactional
    public void uploadFile(MultipartFile file, String type, Integer processId) throws IOException {
        Date now = new Date();
        PDFData pdfData = storageDao.save(PDFData.builder()
                .logDates(LogDates.builder().createDate(now).updateDate(now).build())
                .name(processId+"_"+file.getOriginalFilename())
                .type(file.getContentType())
                .data(PDFUtils.compressPDF(file.getBytes()))
                .build());
        // delete older
        Integer oldPdfId = internshipProcessService.updateFileId(processId, pdfData.getId(), type);

        if(oldPdfId != null){
            storageDao.deleteById(oldPdfId);
        }

        logger.info("File uploaded successfully with id: " + pdfData.getId());
    }

    public byte[] downloadFile(Integer fileId) {
        try {
            Optional<PDFData> dbPdfData = storageDao.findById(fileId);
            return PDFUtils.decompressPDF(dbPdfData.get().getData());
        } catch (Exception e) {
            return null;
        }
    }
}
