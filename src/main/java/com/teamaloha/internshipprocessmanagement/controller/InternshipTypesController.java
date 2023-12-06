package com.teamaloha.internshipprocessmanagement.controller;

import com.teamaloha.internshipprocessmanagement.dto.internshipTypes.InternshipTypesAddRequest;
import com.teamaloha.internshipprocessmanagement.dto.internshipTypes.InternshipTypesRemoveRequest;
import com.teamaloha.internshipprocessmanagement.dto.internshipTypes.InternshipTypesUpdateRequest;
import com.teamaloha.internshipprocessmanagement.service.InternshipTypesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internshipTypes")
public class InternshipTypesController {

    private final InternshipTypesService internshipTypesService;

    @Autowired
    public InternshipTypesController(InternshipTypesService internshipTypesService) {
        this.internshipTypesService = internshipTypesService;
    }

    @PostMapping("/addInternshipType")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<HttpStatus> addInternshipType(@RequestBody InternshipTypesAddRequest internshipTypesAddRequest) {
        return internshipTypesService.addInternshipType(internshipTypesAddRequest);
    }

    @PostMapping("/updateInternshipType")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<HttpStatus> updateInternshipType(@RequestBody InternshipTypesUpdateRequest internshipTypesUpdateRequest) {
        return internshipTypesService.updateInternshipType(internshipTypesUpdateRequest);
    }

    @GetMapping("/removeInternshipType")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<HttpStatus> removeInternshipType(@RequestBody InternshipTypesRemoveRequest internshipTypesRemoveRequest) {
        return internshipTypesService.removeInternshipType(internshipTypesRemoveRequest);
    }

}
