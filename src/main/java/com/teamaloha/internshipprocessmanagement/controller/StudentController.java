package com.teamaloha.internshipprocessmanagement.controller;

import com.teamaloha.internshipprocessmanagement.dto.authentication.AuthenticationRequest;
import com.teamaloha.internshipprocessmanagement.dto.authentication.AuthenticationResponse;
import com.teamaloha.internshipprocessmanagement.dto.authentication.StudentRegisterRequest;
import com.teamaloha.internshipprocessmanagement.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student")
public class StudentController {
    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping("/auth/register")
    @ResponseStatus(HttpStatus.OK)
    public AuthenticationResponse register(@RequestBody @Valid StudentRegisterRequest studentRegisterRequest) {
        return studentService.register(studentRegisterRequest);
    }

    @PostMapping("/auth/login")
    @ResponseStatus(HttpStatus.OK)
    public AuthenticationResponse login(@RequestBody @Valid AuthenticationRequest authenticationRequest) {
        return studentService.login(authenticationRequest);
    }

    @PostMapping("/auth/forgotPassword")
    @ResponseStatus(HttpStatus.OK)
    public void forgotPassword(@RequestParam @Valid String email) {
        studentService.forgotPassword(email);
    }

    @PostMapping("/auth/resetPassword")
    @ResponseStatus(HttpStatus.OK)
    public void resetPassword(@RequestParam @Valid String token, @RequestParam @Valid String newPassword) {
        studentService.resetPassword(token, newPassword);
    }

    @PostMapping("/auth/verify")
    @ResponseStatus(HttpStatus.OK)
    public boolean verify(@RequestParam @Valid String code, @RequestParam @Valid String mail) {
        return studentService.verify(code, mail);
    }
}
