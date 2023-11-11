package com.teamaloha.internshipprocessmanagement.controller;

import com.teamaloha.internshipprocessmanagement.dto.InternshipProcess.InternshipProcessDeleteResponse;
import com.teamaloha.internshipprocessmanagement.dto.InternshipProcess.InternshipProcessDto;
import com.teamaloha.internshipprocessmanagement.dto.InternshipProcess.InternshipProcessInitResponse;
import com.teamaloha.internshipprocessmanagement.dto.InternshipProcess.InternshipProcessUpdateResponse;
import com.teamaloha.internshipprocessmanagement.service.InternshipProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public InternshipProcessInitResponse initInternshipProcess() {
        String mail = "Tugay@gtu.edu.tr";
        return internshipProcessService.initInternshipProcess(mail);
    }

    @PostMapping("/update")
    @ResponseStatus(HttpStatus.OK)
    public InternshipProcessUpdateResponse updateIntershipProcess(InternshipProcessDto internshipProcessDto) {
        return  internshipProcessService.updateInternshipProcess(internshipProcessDto);
    }
    @DeleteMapping("/delete")
    @ResponseStatus(HttpStatus.OK)
    public InternshipProcessDeleteResponse deleteIntershipProcess(Integer internshipProcessID) {
        return  internshipProcessService.deleteInternshipProcess(internshipProcessID);
    }

}
