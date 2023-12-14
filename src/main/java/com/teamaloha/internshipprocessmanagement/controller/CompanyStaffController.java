package com.teamaloha.internshipprocessmanagement.controller;


import com.teamaloha.internshipprocessmanagement.dto.companyStaff.*;
import com.teamaloha.internshipprocessmanagement.service.CompanyStaffService;
import jakarta.validation.Valid;
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
    public CompanyStaffAddResponse addCompanyStaff(@RequestBody CompanyStaffAddRequest companyStaffAddRequest) {
        return companyStaffService.add(companyStaffAddRequest);
    }

    @PostMapping("/updateCompanyStaff")
    @ResponseStatus(HttpStatus.OK)
    // test ten sonra ekleyelim
    // @PreAuthorize("hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).STUDENT.name()) || hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).ACADEMICIAN.name())")
    public CompanyStaffUpdateResponse updateCompanyStaff(@RequestBody CompanyStaffUpdateRequest companyStaffUpdateRequest) {
        return companyStaffService.update(companyStaffUpdateRequest);
    }

    @GetMapping("/getAllByCompany")
    @ResponseStatus(HttpStatus.OK)
    public CompanyStaffGetAllResponse getAllByCompany(@RequestBody @Valid CompanyStaffGetAllByCompanyRequest getAllByCompanyRequest) {
        return companyStaffService.getAllByCompanyId(getAllByCompanyRequest.getCompanyId());
    }
}
