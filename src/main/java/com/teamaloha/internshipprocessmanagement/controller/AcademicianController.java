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

import java.util.List;

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
    public AcademicsGetAllResponse getAllAcademics(@RequestBody AcademicianSearchDto academicianSearchDto, @CurrentUserId Integer adminId) {
        return academicianService.getAllAcademics(academicianSearchDto, adminId);
    }

    // TODO: ADD ADMIN ROLE CONTROL INSTEAD ACADEMICIAN
    @GetMapping("/get-all-not-pageable")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).ACADEMICIAN.name())")
    public AcademicsGetAllResponse getAllAcademics(@CurrentUserId Integer adminId) {
        return academicianService.getAllAcademics(adminId);
    }

    @PutMapping("/validate")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).ACADEMICIAN.name())")
    public void validateAcademician(@RequestParam("academicianId") Integer academicianId, @CurrentUserId Integer adminId) {
        academicianService.validateAcademician(academicianId, adminId);
    }

    @PutMapping("/assign-department")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).ACADEMICIAN.name())")
    public void assignDepartmentToAcademician(@RequestParam("academicianId") Integer academicianId, @RequestParam("departmentId") Integer departmentId, @CurrentUserId Integer adminId) {
        academicianService.assignDepartmentToAcademician(academicianId, departmentId, adminId);
    }

    @PostMapping("/assignTaskOnly")
    @ResponseStatus(HttpStatus.OK)
    public boolean assignTaskOnly(@RequestParam("academicianId") Integer academicianId, @RequestParam("taskId") Integer taskId, @CurrentUserId Integer adminId) {
        return academicianService.assignTask(academicianId, taskId, adminId);
    }

    @PostMapping("/assignTask")
    @ResponseStatus(HttpStatus.OK)
    public boolean assignTask(@RequestParam("academicianId") Integer academicianId, @RequestParam("taskId") List<Integer> taskId, @CurrentUserId Integer adminId) {
        System.out.println(taskId);
        return academicianService.assignTask(academicianId, taskId, adminId);
    }

    @PostMapping("/auth/forgotPassword")
    @ResponseStatus(HttpStatus.OK)
    public void forgotPassword(@RequestParam @Valid String email) {
        academicianService.forgotPassword(email);
    }

    @PostMapping("/auth/resetPassword")
    @ResponseStatus(HttpStatus.OK)
    public void resetPassword(@RequestParam @Valid String token, @RequestParam @Valid String newPassword) {
        academicianService.resetPassword(token, newPassword);
    }

    @PostMapping("/auth/verify")
    @ResponseStatus(HttpStatus.OK)
    public boolean verify(@RequestParam @Valid String code, @RequestParam @Valid String mail) {
        return academicianService.verify(code, mail);
    }
}
