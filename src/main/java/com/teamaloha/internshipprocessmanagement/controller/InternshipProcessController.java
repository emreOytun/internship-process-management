package com.teamaloha.internshipprocessmanagement.controller;

import com.teamaloha.internshipprocessmanagement.annotations.CurrentUserId;
import com.teamaloha.internshipprocessmanagement.dto.InternshipProcess.InternshipProcessDeleteResponse;
import com.teamaloha.internshipprocessmanagement.dto.InternshipProcess.InternshipProcessDto;
import com.teamaloha.internshipprocessmanagement.dto.InternshipProcess.InternshipProcessInitResponse;
import com.teamaloha.internshipprocessmanagement.dto.InternshipProcess.InternshipProcessUpdateResponse;
import com.teamaloha.internshipprocessmanagement.service.InternshipProcessService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("http://localhost:4200")
@RestController
@RequestMapping("/api/internship-process")
public class InternshipProcessController {

    private final InternshipProcessService internshipProcessService;
    @Autowired
    public InternshipProcessController(InternshipProcessService internshipProcessService) {
        this.internshipProcessService = internshipProcessService;
    }

    @PostMapping("/init")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).STUDENT.name())")
    public InternshipProcessInitResponse initInternshipProcess(@CurrentUserId Integer userId) {
        String mail = "Tugay@gtu.edu.tr";
        return internshipProcessService.initInternshipProcess(userId);
    }

    @PostMapping("/update")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).STUDENT.name())")
    public InternshipProcessUpdateResponse updateIntershipProcess(@RequestBody @Valid InternshipProcessDto internshipProcessDto, @CurrentUserId Integer userId) {
        return internshipProcessService.updateInternshipProcess(internshipProcessDto, userId);
    }
    @DeleteMapping("/delete")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).STUDENT.name())")
    public InternshipProcessDeleteResponse deleteIntershipProcess(Integer internshipProcessID) {
        return internshipProcessService.deleteInternshipProcess(internshipProcessID);
    }

}
