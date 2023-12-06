package com.teamaloha.internshipprocessmanagement.controller;

import com.teamaloha.internshipprocessmanagement.annotations.CurrentUserId;
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

    @GetMapping("/get-all")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).ACADEMICIAN.name())")
    public AcademicsGetAllResponse getAllAcademics(@CurrentUserId Integer adminId) {
        return academicianService.getAllAcademics(adminId);
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


}
