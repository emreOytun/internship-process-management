package com.teamaloha.internshipprocessmanagement.service;


import com.teamaloha.internshipprocessmanagement.dao.StorageDao;
import com.teamaloha.internshipprocessmanagement.dto.PDFDataGetResponse;
import com.teamaloha.internshipprocessmanagement.entity.InternshipProcess;
import com.teamaloha.internshipprocessmanagement.entity.PDFData;
import com.teamaloha.internshipprocessmanagement.entity.embeddable.LogDates;
import com.teamaloha.internshipprocessmanagement.exceptions.CustomException;
import com.teamaloha.internshipprocessmanagement.utilities.PDFUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class StorageService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private StorageDao storageDao;
    private InternshipProcessService internshipProcessService;

    private StudentService studentService;
    private AcademicianService academicianService;

    @Autowired
    public StorageService(StorageDao storageDao, InternshipProcessService internshipProcessService, StudentService studentService, AcademicianService academicianService) {
        this.storageDao = storageDao;
        this.internshipProcessService = internshipProcessService;
        this.academicianService = academicianService;
        this.studentService = studentService;
    }

    @Transactional
    public void deleteFile(Integer fileId, String type, Integer processId, Integer userId) {
        InternshipProcess internshipProcess = internshipProcessService.findById(processId);
        if (internshipProcess == null) {
            logger.error("Internship process is not found.");
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
        if (!internshipProcess.getStudent().getId().equals(userId)) {
            logger.error("Internship process student id: " + internshipProcess.getStudent().getId() + " is not equal " +
                    "to given student id: " + userId);
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }

        PDFData pdfData = storageDao.findByIdAndFileOwnerId(fileId, userId);
        if (pdfData == null) {
            logger.info("PDF is not found for given fileId: " + fileId + " and userId: " + userId);
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }

        storageDao.deleteById(fileId);
        internshipProcessService.deleteFileId(internshipProcess, type);
    }

    @Transactional
    public void uploadFile(MultipartFile file, String type, Integer processId, Integer userId) throws IOException {
        if (!("application/pdf".equals(file.getContentType()))) {
            logger.error("File type is not PDF. processId: " + processId + " type: " + file.getContentType());
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }

        InternshipProcess internshipProcess = internshipProcessService.findById(processId);
        if (internshipProcess == null) {
            logger.error("Internship process is not found.");
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
        if (!internshipProcess.getStudent().getId().equals(userId)) {
            logger.error("Internship process student id: " + internshipProcess.getStudent().getId() + " is not equal " +
                    "to given student id: " + userId);
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
        if (type == null || type.length() < 5) {
            logger.error("Invalid type. type: " + type);
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }

        String fileName = type.substring(0, type.length() - 2);
        Date now = new Date();
        PDFData pdfData = storageDao.save(PDFData.builder()
                .logDates(LogDates.builder().createDate(now).updateDate(now).build())
                .name(fileName)
                .type(file.getContentType())
                .data(PDFUtils.compressPDF(file.getBytes()))
                .fileOwnerId(userId)
                .build());

        // delete older
        Integer oldPdfId = internshipProcessService.updateFileIdAndName(internshipProcess, pdfData.getId(), pdfData.getName(), type);

        if(oldPdfId != null){
            storageDao.deleteById(oldPdfId);
        }

        logger.info("File uploaded successfully with id: " + pdfData.getId());
    }

    public byte[] downloadFileAcademician(Integer fileId, Integer userId) {
        PDFData pdfData = storageDao.findPDFDataById(fileId);
        if (pdfData != null) {
            return PDFUtils.decompressPDF(pdfData.getData());
        }
        return null;
    }

    public byte[] downloadFileStudent(Integer fileId, Integer userId) {
            PDFData pdfData = storageDao.findPDFDataById(fileId);
            if (pdfData != null && !pdfData.getFileOwnerId().equals(userId)) {
                logger.error("User ID and PDFData Owner Id does not match. userId: " + userId + " PDFData Owner Id: " + pdfData.getFileOwnerId());
                throw new CustomException(HttpStatus.BAD_REQUEST);
            }

            if(pdfData != null) {
                return PDFUtils.decompressPDF(pdfData.getData());
            }
            return null;
    }
}
