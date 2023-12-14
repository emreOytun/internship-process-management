package com.teamaloha.internshipprocessmanagement.controller;

import com.teamaloha.internshipprocessmanagement.dto.company.*;
import com.teamaloha.internshipprocessmanagement.dto.company.CompanyGetAllResponse;
import com.teamaloha.internshipprocessmanagement.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/company")
public class CompanyController {

    private final CompanyService companyService;

    @Autowired
    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }
    @PostMapping("/addCompany")
    @ResponseStatus(HttpStatus.OK)
    // test ten sonra ekleyelim
    // @PreAuthorize("hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).STUDENT.name()) || hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).ACADEMICIAN.name())")
    public CompanyAddResponse addCompany(@RequestBody CompanyAddRequest companyAddRequest) {
        return companyService.add(companyAddRequest);
    }

    @PostMapping("/updateCompany")
    @ResponseStatus(HttpStatus.OK)
    // test ten sonra ekleyelim
    // @PreAuthorize("hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).STUDENT.name()) || hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).ACADEMICIAN.name())")
    public CompanyUpdateResponse updateCompany(@RequestBody CompanyUpdateRequest companyUpdateRequest) {
        return companyService.update(companyUpdateRequest);
    }

    @GetMapping("/getCompany")
    @ResponseStatus(HttpStatus.OK)
    // test ten sonra ekleyelim
    // @PreAuthorize("hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).STUDENT.name()) || hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).ACADEMICIAN.name())")
    public CompanyGetResponse updateCompany(@RequestBody CompanyGetRequest companyGetRequest) {
        return companyService.get(companyGetRequest);
    }

    @GetMapping("/getAll")
    @ResponseStatus(HttpStatus.OK)
    public CompanyGetAllResponse getAll() {
        return companyService.getAll();
    }
}
