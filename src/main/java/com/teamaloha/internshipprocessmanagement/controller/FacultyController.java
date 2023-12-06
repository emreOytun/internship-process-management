package com.teamaloha.internshipprocessmanagement.controller;

import com.teamaloha.internshipprocessmanagement.dto.company.CompanyUpdateRequest;
import com.teamaloha.internshipprocessmanagement.dto.company.CompanyUpdateResponse;
import com.teamaloha.internshipprocessmanagement.dto.faculty.FacultyAddRequest;
import com.teamaloha.internshipprocessmanagement.dto.faculty.FacultyUpdateRequest;
import com.teamaloha.internshipprocessmanagement.dto.faculty.FacultyUpdateResponse;
import com.teamaloha.internshipprocessmanagement.dto.holiday.HolidayAddRequest;
import com.teamaloha.internshipprocessmanagement.service.FacultyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public void addHoliday(@RequestBody FacultyAddRequest facultyAddRequest) {
        facultyService.addFaculty(facultyAddRequest);
    }

    @PostMapping("/updateFaculty")
    @ResponseStatus(HttpStatus.OK)
    // test ten sonra ekleyelim
    // @PreAuthorize("hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).STUDENT.name()) || hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).ACADEMICIAN.name())")
    public FacultyUpdateResponse updateFaculty(@RequestBody FacultyUpdateRequest facultyUpdateRequest) {
        return facultyService.update(facultyUpdateRequest);
    }
}
