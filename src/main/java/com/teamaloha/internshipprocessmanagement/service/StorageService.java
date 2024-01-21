package com.teamaloha.internshipprocessmanagement.service;


import com.teamaloha.internshipprocessmanagement.dao.StorageDao;
import com.teamaloha.internshipprocessmanagement.entity.Academician;
import com.teamaloha.internshipprocessmanagement.entity.InternshipProcess;
import com.teamaloha.internshipprocessmanagement.entity.PDFData;
import com.teamaloha.internshipprocessmanagement.entity.Student;
import com.teamaloha.internshipprocessmanagement.utilities.PDFUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@Service
public class StorageService {
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
    public void uploadFile(MultipartFile file, String type, Integer processId) throws IOException {
        PDFData pdfData = storageDao.save(PDFData.builder()
                .name(processId+"_"+file.getOriginalFilename())
                .type(file.getContentType())
                .data(PDFUtils.compressPDF(file.getBytes()))
                .build());
        // delete older
        Integer oldPdfId = internshipProcessService.updateFileId(processId, pdfData.getId(), type);

        if(oldPdfId != null){
            storageDao.deleteById(oldPdfId);
        }
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
