package com.teamaloha.internshipprocessmanagement.controller;

import com.teamaloha.internshipprocessmanagement.dto.department.DepartmentAddRequest;
import com.teamaloha.internshipprocessmanagement.dto.department.DepartmentGetAllResponse;
import com.teamaloha.internshipprocessmanagement.dto.department.DepartmentUpdateRequest;
import com.teamaloha.internshipprocessmanagement.dto.department.DepartmentUpdateResponse;
import com.teamaloha.internshipprocessmanagement.service.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/department")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping("/getAll")
    @ResponseStatus(HttpStatus.OK)
    public DepartmentGetAllResponse getAll() {
        return departmentService.getAll();
    }

    @PostMapping("/addDepartment")
    @ResponseStatus(HttpStatus.OK)
    public void addHoliday(@RequestBody @Valid DepartmentAddRequest departmentAddRequest) {
        departmentService.addDepartment(departmentAddRequest);
    }

    @PostMapping("/updateDepartment")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).STUDENT.name()) || hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).ACADEMICIAN.name())")
    public DepartmentUpdateResponse updateFaculty(@RequestBody @Valid DepartmentUpdateRequest departmentUpdateRequest) {
        return departmentService.update(departmentUpdateRequest);
    }
}
