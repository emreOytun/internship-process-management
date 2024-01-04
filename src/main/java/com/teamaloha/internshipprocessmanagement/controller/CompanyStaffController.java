package com.teamaloha.internshipprocessmanagement.controller;


import com.teamaloha.internshipprocessmanagement.dto.companyStaff.*;
import com.teamaloha.internshipprocessmanagement.service.CompanyStaffService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/staff")
public class CompanyStaffController {
    private final CompanyStaffService companyStaffService;

    @Autowired
    public CompanyStaffController(CompanyStaffService companyStaffService) {
        this.companyStaffService = companyStaffService;
    }
    @PostMapping("/addCompanyStaff")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).STUDENT.name()) || hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).ACADEMICIAN.name())")
    public CompanyStaffAddResponse addCompanyStaff(@RequestBody @Valid CompanyStaffAddRequest companyStaffAddRequest) {
        return companyStaffService.add(companyStaffAddRequest);
    }

    @PostMapping("/updateCompanyStaff")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).STUDENT.name()) || hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).ACADEMICIAN.name())")
    public CompanyStaffUpdateResponse updateCompanyStaff(@RequestBody @Valid CompanyStaffUpdateRequest companyStaffUpdateRequest) {
        return companyStaffService.update(companyStaffUpdateRequest);
    }

    @GetMapping("/getAllByCompany")
    @ResponseStatus(HttpStatus.OK)
    public CompanyStaffGetAllResponse getAllByCompany(@RequestParam @Valid Integer companyId ) {
        return companyStaffService.getAllByCompanyId(companyId);
    }
}
