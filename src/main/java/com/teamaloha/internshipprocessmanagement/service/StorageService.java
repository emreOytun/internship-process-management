package com.teamaloha.internshipprocessmanagement.service;


import com.teamaloha.internshipprocessmanagement.dao.StorageDao;
import com.teamaloha.internshipprocessmanagement.entity.PDFData;
import com.teamaloha.internshipprocessmanagement.entity.Student;
import com.teamaloha.internshipprocessmanagement.entity.embeddable.LogDates;
import com.teamaloha.internshipprocessmanagement.exceptions.CustomException;
import com.teamaloha.internshipprocessmanagement.utilities.PDFUtils;
import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
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
    public void uploadFile(MultipartFile file, String type, Integer processId, Integer userId) throws IOException {
        if (!("application/pdf".equals(file.getContentType()))) {
            logger.error("File type is not PDF. processId: " + processId + " type: " + file.getContentType());
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
        Date now = new Date();
        PDFData pdfData = storageDao.save(PDFData.builder()
                .logDates(LogDates.builder().createDate(now).updateDate(now).build())
                .name(processId+"_"+file.getOriginalFilename())
                .type(file.getContentType())
                .data(PDFUtils.compressPDF(file.getBytes()))
                .fileOwnerId(userId)
                .build());

        // delete older
        Integer oldPdfId = internshipProcessService.updateFileId(processId, pdfData.getId(), type);

        if(oldPdfId != null){
            storageDao.deleteById(oldPdfId);
        }

        logger.info("File uploaded successfully with id: " + pdfData.getId());
    }

    public byte[] downloadFileAcademician(Integer fileId, Integer userId) {
        try {
            String academicianName = academicianService.getAcademicianNameById(userId);

            if(academicianName == null)
                return null;

            Optional<PDFData> dbPdfData = storageDao.findById(fileId);

            return dbPdfData.map(pdfData -> PDFUtils.decompressPDF(pdfData.getData())).orElse(null);

        } catch (Exception e) {
            return null;
        }
    }

    public byte[] downloadFileStudent(Integer fileId, Integer userId) {
        try {
            Student student = (Student) studentService.findStudentById(userId);

            if(student == null)
                return null;

            Optional<PDFData> dbPdfData = storageDao.findById(fileId);

            if(dbPdfData.isPresent()) {
                PDFData pdfData = dbPdfData.get();
                if (Objects.equals(student.getId(), pdfData.getFileOwnerId())){
                    return PDFUtils.decompressPDF(pdfData.getData());
                }
            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }

}
