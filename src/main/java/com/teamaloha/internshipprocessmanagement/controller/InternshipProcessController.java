package com.teamaloha.internshipprocessmanagement.controller;

import com.teamaloha.internshipprocessmanagement.annotations.CurrentUserId;
import com.teamaloha.internshipprocessmanagement.dto.InternshipProcess.InternshipProcessUpdateRequest;
import com.teamaloha.internshipprocessmanagement.dto.InternshipProcess.InternshipProcessInitResponse;
import com.teamaloha.internshipprocessmanagement.service.InternshipProcessService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
        return internshipProcessService.initInternshipProcess(userId);
    }

    @PutMapping("/update")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).STUDENT.name())")
    public void updateInternshipProcess(@RequestBody @Valid InternshipProcessUpdateRequest internshipProcessUpdateRequest, @CurrentUserId Integer userId) {
        internshipProcessService.updateInternshipProcess(internshipProcessUpdateRequest, userId);
    }
    @DeleteMapping("/delete")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).STUDENT.name())")
    public void deleteInternshipProcess(Integer internshipProcessID) {
        internshipProcessService.deleteInternshipProcess(internshipProcessID);
    }

    @PostMapping("/start-internship-process-approval")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).STUDENT.name())")
    public void startInternshipApprovalProcess(@RequestParam("processId") Integer internshipProcessID, @CurrentUserId Integer userId) {
        internshipProcessService.startInternshipApprovalProcess(internshipProcessID, userId);
    }

}
