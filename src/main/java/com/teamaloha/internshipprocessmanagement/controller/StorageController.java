package com.teamaloha.internshipprocessmanagement.controller;

import com.teamaloha.internshipprocessmanagement.annotations.CurrentUserId;
import com.teamaloha.internshipprocessmanagement.dto.PDFDataGetResponse;
import com.teamaloha.internshipprocessmanagement.service.StorageService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/file")
public class StorageController {

    private final StorageService storageService;

    @Autowired
    public StorageController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping("/upload")
    @PreAuthorize("hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).STUDENT.name())")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, @NotBlank String type, @NotNull Integer processId, @CurrentUserId Integer userId) throws IOException {
        storageService.uploadFile(file, type, processId, userId);
        return ResponseEntity.status(HttpStatus.OK).body("File uploaded successfully");
    }

    @GetMapping("/downloadAcademician")
    @PreAuthorize("hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).ACADEMICIAN.name())")
    public ResponseEntity<?> downloadFileAcademician(@RequestParam Integer fileId, @CurrentUserId Integer userId) {
        byte[] downloadedFile = storageService.downloadFileAcademician(fileId, userId);
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.valueOf("application/pdf")).body(downloadedFile);
    }

    @GetMapping("/downloadStudent")
    @PreAuthorize("hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).STUDENT.name())")
    public ResponseEntity<?> downloadFileStudent(@RequestParam @NotNull Integer fileId, @CurrentUserId Integer userId) {
        byte[] downloadedFile = storageService.downloadFileStudent(fileId, userId);
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.valueOf("application/pdf")).body(downloadedFile);
    }

    @GetMapping("/deleteFile")
    @PreAuthorize("hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).STUDENT.name())")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFile(@RequestParam @NotNull Integer fileId, @NotBlank String type, @NotNull Integer processId, @CurrentUserId Integer userId) {
        storageService.deleteFile(fileId, type, processId, userId);
    }
}
