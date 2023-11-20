package com.teamaloha.internshipprocessmanagement.controller;


import com.teamaloha.internshipprocessmanagement.dto.company.*;
import com.teamaloha.internshipprocessmanagement.dto.companyStaff.CompanyStaffAddRequest;
import com.teamaloha.internshipprocessmanagement.dto.companyStaff.CompanyStaffAddResponse;
import com.teamaloha.internshipprocessmanagement.dto.companyStaff.CompanyStaffUpdateRequest;
import com.teamaloha.internshipprocessmanagement.dto.companyStaff.CompanyStaffUpdateResponse;
import com.teamaloha.internshipprocessmanagement.service.CompanyService;
import com.teamaloha.internshipprocessmanagement.service.CompanyStaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    // test ten sonra ekleyelim
    // @PreAuthorize("hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).STUDENT.name()) || hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).ACADEMICIAN.name())")
    public CompanyStaffAddResponse addCompany(@RequestBody CompanyStaffAddRequest companyStaffAddRequest) {
        return companyStaffService.add(companyStaffAddRequest);
    }

    @PostMapping("/updateCompanyStaff")
    @ResponseStatus(HttpStatus.OK)
    // test ten sonra ekleyelim
    // @PreAuthorize("hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).STUDENT.name()) || hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).ACADEMICIAN.name())")
    public CompanyStaffUpdateResponse updateCompany(@RequestBody CompanyStaffUpdateRequest companyStaffUpdateRequest) {
        return companyStaffService.update(companyStaffUpdateRequest);
    }


}
