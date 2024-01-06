package com.teamaloha.internshipprocessmanagement.service;


import com.teamaloha.internshipprocessmanagement.dao.StorageDao;
import com.teamaloha.internshipprocessmanagement.entity.PDFData;
import com.teamaloha.internshipprocessmanagement.utilities.PDFUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
public class StorageService {
    private StorageDao storageDao;
    @Autowired
    public StorageService(StorageDao storageDao) {
        this.storageDao = storageDao;
    }

    public String uploadFile(MultipartFile file){
        String message = "";
        try {

            storageDao.save(PDFData.builder()
                    .name(file.getOriginalFilename())
                    .type(file.getContentType())
                    .data(PDFUtils.compressPDF(file.getBytes()))
                    .build());

            message = "Uploaded the file successfully: " + file.getOriginalFilename();
            return message;
        } catch (Exception e) {
            message = "Could not upload the file: " + file.getOriginalFilename() + "!";
            return null;
        }
    }

    public byte[] downloadFile(String fileName) {
        try {
            Optional<PDFData> dbPdfData = storageDao.findByName(fileName);
            return PDFUtils.decompressPDF(dbPdfData.get().getData());
        } catch (Exception e) {
            return null;
        }
    }
}
