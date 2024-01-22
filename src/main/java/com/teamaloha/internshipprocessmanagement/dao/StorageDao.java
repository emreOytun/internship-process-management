package com.teamaloha.internshipprocessmanagement.dao;

import com.teamaloha.internshipprocessmanagement.entity.PDFData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StorageDao extends JpaRepository<PDFData, Integer> {

    Optional<PDFData> findByName(String name);
    PDFData findPDFDataByById(Integer id);
    PDFData findByIdAndFileOwnerId(Integer fileId, Integer ownerId);
}
