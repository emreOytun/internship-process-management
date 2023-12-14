package com.teamaloha.internshipprocessmanagement.controller;

import com.teamaloha.internshipprocessmanagement.dto.department.DepartmentAddRequest;
import com.teamaloha.internshipprocessmanagement.dto.department.DepartmentGetAllResponse;
import com.teamaloha.internshipprocessmanagement.dto.department.DepartmentUpdateRequest;
import com.teamaloha.internshipprocessmanagement.dto.department.DepartmentUpdateResponse;
import com.teamaloha.internshipprocessmanagement.dto.faculty.FacultyUpdateRequest;
import com.teamaloha.internshipprocessmanagement.dto.faculty.FacultyUpdateResponse;
import com.teamaloha.internshipprocessmanagement.dto.holiday.HolidayAddRequest;
import com.teamaloha.internshipprocessmanagement.service.DepartmentService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
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
    public void addHoliday(@RequestBody DepartmentAddRequest departmentAddRequest) {
        departmentService.addDepartment(departmentAddRequest);
    }

    @PostMapping("/updateDepartment")
    @ResponseStatus(HttpStatus.OK)
    // test ten sonra ekleyelim
    // @PreAuthorize("hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).STUDENT.name()) || hasAuthority(T(com.teamaloha.internshipprocessmanagement.enums.RoleEnum).ACADEMICIAN.name())")
    public DepartmentUpdateResponse updateFaculty(@RequestBody DepartmentUpdateRequest departmentUpdateRequest) {
        return departmentService.update(departmentUpdateRequest);
    }
}
