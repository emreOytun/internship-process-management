package com.teamaloha.internshipprocessmanagement.controller;

import com.teamaloha.internshipprocessmanagement.dto.faculty.FacultyAddRequest;
import com.teamaloha.internshipprocessmanagement.dto.faculty.FacultyUpdateRequest;
import com.teamaloha.internshipprocessmanagement.dto.faculty.FacultyUpdateResponse;
import com.teamaloha.internshipprocessmanagement.service.FacultyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/faculty")
public class FacultyController {

    private final FacultyService facultyService;

    @Autowired
    public FacultyController(FacultyService facultyService){
        this.facultyService = facultyService;
    }

    @PostMapping("/addFaculty")
    @ResponseStatus(HttpStatus.OK)
    public void addHoliday(@RequestBody @Valid FacultyAddRequest facultyAddRequest) {
        facultyService.addFaculty(facultyAddRequest);
    }

    @PostMapping("/updateFaculty")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).STUDENT.name()) || hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).ACADEMICIAN.name())")
    public FacultyUpdateResponse updateFaculty(@RequestBody @Valid FacultyUpdateRequest facultyUpdateRequest) {
        return facultyService.update(facultyUpdateRequest);
    }
}
