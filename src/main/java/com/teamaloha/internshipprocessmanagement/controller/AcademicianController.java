package com.teamaloha.internshipprocessmanagement.controller;

import com.teamaloha.internshipprocessmanagement.dto.authentication.AcademicianRegisterRequest;
import com.teamaloha.internshipprocessmanagement.dto.authentication.AuthenticationRequest;
import com.teamaloha.internshipprocessmanagement.dto.authentication.AuthenticationResponse;
import com.teamaloha.internshipprocessmanagement.service.AcademicianService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/academician")
public class AcademicianController {
    private AcademicianService academicianService;

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
}
