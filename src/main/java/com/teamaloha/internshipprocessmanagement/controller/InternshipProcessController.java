package com.teamaloha.internshipprocessmanagement.controller;

import com.teamaloha.internshipprocessmanagement.annotations.CurrentUserId;
import com.teamaloha.internshipprocessmanagement.dto.InternshipProcess.*;
import com.teamaloha.internshipprocessmanagement.dto.academician.AcademicsGetStudentAllProcessResponse;
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

    @GetMapping("/get-all")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).STUDENT.name())")
    public InternshipProcessGetAllResponse getAllInternshipProcess(@CurrentUserId Integer userId) {
        return internshipProcessService.getAllInternshipProcess(userId);
    }

    @GetMapping("/get")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).STUDENT.name())")
    public InternshipProcessGetResponse getInternshipProcess(@RequestParam("processId") Integer internshipProcessID,
                                                             @CurrentUserId Integer userId) {
        return internshipProcessService.getInternshipProcess(internshipProcessID, userId);
    }

    @GetMapping("/get-student-all-processes")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).ACADEMICIAN.name())")
    public AcademicsGetStudentAllProcessResponse getStudentAllProcess(@RequestParam("studentId") Integer studentId, @CurrentUserId Integer academicianId) {
        return internshipProcessService.getStudentAllProcess(studentId, academicianId);
    }

    @PutMapping("/update")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).STUDENT.name())")
    public void updateInternshipProcess(@RequestBody @Valid InternshipProcessUpdateRequest internshipProcessUpdateRequest,
                                        @CurrentUserId Integer userId) {
        internshipProcessService.updateInternshipProcess(internshipProcessUpdateRequest, userId);
    }

    @DeleteMapping("/delete")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).STUDENT.name())")
    public void deleteInternshipProcess(Integer internshipProcessID) {
        internshipProcessService.deleteInternshipProcess(internshipProcessID);
    }

    @PostMapping("/start")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).STUDENT.name())")
    public void startInternshipApprovalProcess(@RequestParam("processId") Integer internshipProcessID, @CurrentUserId Integer userId) {
        internshipProcessService.startInternshipApprovalProcess(internshipProcessID, userId);
    }


    @PostMapping("/evaluate")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).ACADEMICIAN.name())")
    public void evaluateInternshipProcess(@RequestBody @Valid InternshipProcessEvaluateRequest internshipProcessEvaluateRequest,
                                          @CurrentUserId Integer userId) {
        internshipProcessService.evaluateInternshipProcess(internshipProcessEvaluateRequest, userId);
    }

    @PostMapping("/cancel")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).STUDENT.name())")
    public void internshipCancellationRequest(@RequestParam("processId") Integer internshipProcessID, @CurrentUserId Integer userId) {
        internshipProcessService.internshipCancellationRequest(internshipProcessID, userId);
    }

    // Internship Extension Request
    @PostMapping("/extension")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).STUDENT.name())")
    public void internshipExtensionRequest(@RequestBody @Valid InternshipExtensionRequestDto internshipExtensionRequestDto, @CurrentUserId Integer userId) {
        internshipProcessService.internshipExtensionRequest(internshipExtensionRequestDto, userId);
    }

    @PostMapping("/get-assigned-process")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).ACADEMICIAN.name())")
    public InternshipProcessGetAllResponse getAssignedInternshipProcess(@RequestBody InternshipProcessSearchDto searchDto, @CurrentUserId Integer academicianId) {
        return internshipProcessService.getAssignedInternshipProcess(academicianId, searchDto);
    }

}
