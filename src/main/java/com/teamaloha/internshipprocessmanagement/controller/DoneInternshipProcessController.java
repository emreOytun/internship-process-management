package com.teamaloha.internshipprocessmanagement.controller;

import com.teamaloha.internshipprocessmanagement.annotations.CurrentUserId;
import com.teamaloha.internshipprocessmanagement.dto.InternshipProcess.ExportRequest;
import com.teamaloha.internshipprocessmanagement.dto.InternshipProcess.InternshipProcessGetAllResponse;
import com.teamaloha.internshipprocessmanagement.dto.doneInternshipProcess.DoneInternshipProcessGetAllResponse;
import com.teamaloha.internshipprocessmanagement.dto.doneInternshipProcess.DoneInternshipProcessGetResponse;
import com.teamaloha.internshipprocessmanagement.dto.faculty.FacultyAddRequest;
import com.teamaloha.internshipprocessmanagement.service.DoneInternshipProcessService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/done-internship-process")
public class DoneInternshipProcessController {

    private final DoneInternshipProcessService doneInternshipProcessService;

    public DoneInternshipProcessController(DoneInternshipProcessService doneInternshipProcessService) {
        this.doneInternshipProcessService = doneInternshipProcessService;
    }

    @GetMapping("/get-all")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).STUDENT.name())")
    public DoneInternshipProcessGetAllResponse getAllDoneInternshipProcess(@CurrentUserId Integer userId) {
        return doneInternshipProcessService.getAllDoneInternshipProcess(userId);
    }

    @GetMapping("/get")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).STUDENT.name())")
    public DoneInternshipProcessGetResponse getDoneInternshipProcess(@RequestParam("processId") Integer internshipProcessID,
                                                                 @CurrentUserId Integer userId) {
        return doneInternshipProcessService.getDoneInternshipProcess(internshipProcessID, userId);
    }

    @GetMapping("/export-excel")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).ACADEMICIAN.name())")
    public void exportExcel(@RequestBody @Valid ExportRequest exportRequest) {
        doneInternshipProcessService.exportExcel(exportRequest);
    }

    @GetMapping("/export-pdf")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).ACADEMICIAN.name())")
    public void exportPdf(@RequestBody @Valid ExportRequest exportRequest) {
        doneInternshipProcessService.exportPdf(exportRequest);
    }

}
