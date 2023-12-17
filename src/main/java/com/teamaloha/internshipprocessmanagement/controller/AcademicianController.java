package com.teamaloha.internshipprocessmanagement.controller;

import com.teamaloha.internshipprocessmanagement.annotations.CurrentUserId;
import com.teamaloha.internshipprocessmanagement.dto.academician.AcademicianSearchDto;
import com.teamaloha.internshipprocessmanagement.dto.academician.AcademicsGetAllResponse;
import com.teamaloha.internshipprocessmanagement.dto.authentication.AcademicianRegisterRequest;
import com.teamaloha.internshipprocessmanagement.dto.authentication.AuthenticationRequest;
import com.teamaloha.internshipprocessmanagement.dto.authentication.AuthenticationResponse;
import com.teamaloha.internshipprocessmanagement.service.AcademicianService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/academician")
public class AcademicianController {
    private final AcademicianService academicianService;

    @Autowired
    public AcademicianController(AcademicianService academicianService) {
        this.academicianService = academicianService;
    }

    @PostMapping("/auth/register")
    @ResponseStatus(HttpStatus.OK)
    public AuthenticationResponse register(@RequestBody @Valid AcademicianRegisterRequest academicianRegisterRequest) {
        return academicianService.register(academicianRegisterRequest);
    }

    @PostMapping("/auth/login")
    @ResponseStatus(HttpStatus.OK)
    public AuthenticationResponse login(@RequestBody @Valid AuthenticationRequest authenticationRequest) {
        return academicianService.login(authenticationRequest);
    }

    // TODO: ADD ADMIN ROLE CONTROL INSTEAD ACADEMICIAN
    @GetMapping("/get-all")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).ACADEMICIAN.name())")
    public AcademicsGetAllResponse getAllAcademics(@RequestBody AcademicianSearchDto academicianSearchDto) {
        return academicianService.getAllAcademics(academicianSearchDto);
    }

    // TODO: ADD ADMIN ROLE CONTROL INSTEAD ACADEMICIAN
    @GetMapping("/get-all-not-pageable")
    @ResponseStatus(HttpStatus.OK)
    public AcademicsGetAllResponse getAllAcademics() {
        return academicianService.getAllAcademics();
    }

    // TODO : change Authority to admin if it is possible
    @PutMapping("/validate")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).ACADEMICIAN.name())")
    public void validateAcademician(@RequestParam("academicianId") Integer academicianId, @CurrentUserId Integer adminId) {
        academicianService.validateAcademician(academicianId, adminId);
    }

    // TODO : change Authority to admin if it is possible
    @PutMapping("/assign-department")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).ACADEMICIAN.name())")
    public void assignDepartmentToAcademician(@RequestParam("academicianId") Integer academicianId, @RequestParam("departmentId") Integer departmentId, @CurrentUserId Integer adminId) {
        academicianService.assignDepartmentToAcademician(academicianId, departmentId, adminId);
    }

    @PostMapping("/assignTask")
    @ResponseStatus(HttpStatus.OK)
    public boolean assignTask(@RequestParam("academicianId") Integer academicianId, @RequestParam("taskId") Integer taskId) {
        return academicianService.assignTask(academicianId, taskId);
    }
}
