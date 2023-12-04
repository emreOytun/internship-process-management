package com.teamaloha.internshipprocessmanagement.service;

import com.teamaloha.internshipprocessmanagement.dao.InternshipProcessDao;
import com.teamaloha.internshipprocessmanagement.dto.InternshipProcess.*;
import com.teamaloha.internshipprocessmanagement.entity.*;
import com.teamaloha.internshipprocessmanagement.entity.embeddable.LogDates;
import com.teamaloha.internshipprocessmanagement.enums.ProcessStatusEnum;
import com.teamaloha.internshipprocessmanagement.exceptions.CustomException;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class InternshipProcessService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final InternshipProcessDao internshipProcessDao;
    private final DepartmentService departmentService;
    private final CompanyService companyService;
    private final AcademicianService academicianService;
    private final ProcessAssigneeService processAssigneeService;
    private InternshipProcessService self ;
    private final ApplicationContext applicationContext;

    @Autowired
    public InternshipProcessService(InternshipProcessDao internshipProcessDao, DepartmentService departmentService,
                                    CompanyService companyService, AcademicianService academicianService,
                                    ProcessAssigneeService processAssigneeService,
                                    ApplicationContext applicationContext) {
        this.internshipProcessDao = internshipProcessDao;
        this.departmentService = departmentService;
        this.companyService = companyService;
        this.academicianService = academicianService;
        this.processAssigneeService = processAssigneeService;
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    private void init() {
        self = applicationContext.getBean(InternshipProcessService.class);
    }


    public InternshipProcessInitResponse initInternshipProcess(Integer studentId) {
        // Only setting the ID of Student entity is enough to insert InternshipProcess entity.
        Student student = new Student();
        student.setId(studentId);

        Date now = new Date();
        InternshipProcess emptyProcess = new InternshipProcess();
        emptyProcess.setStudent(student);
        emptyProcess.setLogDates(LogDates.builder().createDate(now).updateDate(now).build());
        emptyProcess.setProcessStatus(ProcessStatusEnum.FORM);
        emptyProcess.setEditable(true);
        InternshipProcess savedProcess = internshipProcessDao.save(emptyProcess);

        logger.info("Created InternshipProcess with ID: " + savedProcess.getId());
        return new InternshipProcessInitResponse(savedProcess.getId());
    }

    public InternshipProcessGetAllResponse getAllInternshipProcess(Integer userId) {
        Student student = new Student();
        student.setId(userId);

        List<InternshipProcess> internshipProcessList = internshipProcessDao.findAllByStudent(student);

        return new InternshipProcessGetAllResponse(internshipProcessList);
    }

    public InternshipProcessGetResponse getInternshipProcess(Integer internshipProcessID, Integer studentId) {
        // Check if the process exists
        InternshipProcess internshipProcess = getInternshipProcessIfExistsOrThrowException(internshipProcessID);

        // Check if the current user id and the student id of the given internship process is matching.
        checkIfStudentIdAndInternshipProcessMatchesOrThrowException(studentId, internshipProcess.getStudent().getId());

        InternshipProcessGetResponse internshipProcessGetResponse = new InternshipProcessGetResponse();

        copyEntityToDto(internshipProcess, internshipProcessGetResponse);

        return internshipProcessGetResponse;

    }

    public void updateInternshipProcess(InternshipProcessUpdateRequest internshipProcessUpdateRequest, Integer studentId) {
        Integer processId = internshipProcessUpdateRequest.getId();

        InternshipProcess internshipProcess = getInternshipProcessIfExistsOrThrowException(processId);
        if (!internshipProcess.getEditable()) {
            logger.error("Internship process is not editable. Process id: " + processId);
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }

        checkIfStudentIdAndInternshipProcessMatchesOrThrowException(studentId, internshipProcess.getStudent().getId());

        // If department id is given, check if there is such department.
        Department department = null;
        if (internshipProcessUpdateRequest.getDepartmentId() != null) {
            department = departmentService.findDepartmentById(internshipProcessUpdateRequest.getDepartmentId());
            if (department == null) {
                logger.error("Department with given id cannot be found. Department id: "
                        + internshipProcessUpdateRequest.getDepartmentId());
                throw new CustomException(HttpStatus.BAD_REQUEST);
            }
        }

        // If company id is given, check if there is such company.
        Company company = null;
        if (internshipProcessUpdateRequest.getCompanyId() != null) {
            company = companyService.findCompanyById(internshipProcessUpdateRequest.getCompanyId());
            if (company == null) {
                logger.error("Company with given id cannot be found. Company id: "
                        + internshipProcessUpdateRequest.getCompanyId());
                throw new CustomException(HttpStatus.BAD_REQUEST);
            }
        }

        copyDtoToEntity(internshipProcess, internshipProcessUpdateRequest, department, company);
        InternshipProcess updatedInternshipProcess = internshipProcessDao.save(internshipProcess);

        logger.info("Updated InternshipProcess with ID: " + updatedInternshipProcess.getId());
    }

    // TODO: Ne zaman silebilir? Ornegin staj sureci baslamissa silinemez gibi bir kontrol eklenebilir.
    public void deleteInternshipProcess(Integer processId) {
        internshipProcessDao.deleteById(processId);
        logger.info("Deleted InternshipProcess with ID: " + processId);
    }

    public void startInternshipApprovalProcess(Integer processId, Integer studentId) {
        InternshipProcess internshipProcess = getInternshipProcessIfExistsOrThrowException(processId);
        checkIfStudentIdAndInternshipProcessMatchesOrThrowException(studentId, internshipProcess.getStudent().getId());
        checkIfProcessStatusMatchesOrThrowException(ProcessStatusEnum.FORM, internshipProcess.getProcessStatus());

        if (!areFormFieldsEntered(internshipProcess)) {
            logger.error("The form fields are not completed properly to start internship approval process.");
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }

        Date now = new Date();

        // Assign the process to assignees
        List<ProcessAssignee> assigneeList = prepareProcessAssigneeList(internshipProcess, now);

        // Set updated process status field
        ProcessStatusEnum nextStatus = ProcessStatusEnum.findNextStatus(internshipProcess.getProcessStatus());
        internshipProcess.setProcessStatus(nextStatus);
        internshipProcess.setEditable(false);
        internshipProcess.setAssignerId(studentId);
        internshipProcess.getLogDates().setUpdateDate(now);

        self.insertProcessAssigneesAndUpdateProcessStatus(assigneeList, internshipProcess);
    }

    public void evaluateInternshipProcess(InternshipProcessEvaluateRequest internshipProcessEvaluateRequest,
                                          Integer academicianId) {
        Integer processId = internshipProcessEvaluateRequest.getProcessId();

        if (!internshipProcessEvaluateRequest.getApprove() &&
                StringUtils.isBlank(internshipProcessEvaluateRequest.getComment())) {
            logger.error("Rejection without comment. AcademicianId: " + academicianId + " ProcessId: " + processId);
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }

        boolean isAssignee = processAssigneeService.existsByProcessIdAndAssigneeId(processId, academicianId);
        if (!isAssignee) {
            logger.error("Internship process evaluation tried without being a real assignee for the process.");
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }

        InternshipProcess internshipProcess = getInternshipProcessIfExistsOrThrowException(processId);

        Date now = new Date();
        ProcessStatusEnum nextStatus = null;
        List<ProcessAssignee> assigneeList = null;
        if (!internshipProcessEvaluateRequest.getApprove()) {
            // Rejection
            assigneeList = new ArrayList<>();
            internshipProcess.setAssignerId(academicianId);
            internshipProcess.getLogDates().setUpdateDate(now);
            nextStatus = ProcessStatusEnum.REJECTED;
        }
        else {
            // Approval
            assigneeList = prepareProcessAssigneeList(internshipProcess, now);
            internshipProcess.setAssignerId(academicianId);
            internshipProcess.getLogDates().setUpdateDate(now);
            nextStatus = ProcessStatusEnum.findNextStatus(internshipProcess.getProcessStatus());
        }

        internshipProcess.setProcessStatus(nextStatus);
        internshipProcess.setEditable(isNextStatusEditable(nextStatus));
        self.insertProcessAssigneesAndUpdateProcessStatus(assigneeList, internshipProcess);
    }

    private InternshipProcess getInternshipProcessIfExistsOrThrowException(Integer processId) {
        InternshipProcess internshipProcess = internshipProcessDao.findInternshipProcessById(processId);
        if (internshipProcess == null) {
            logger.error("InternshipProcess with ID " + processId + " not found for update.");
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
        return internshipProcess;
    }

    private void checkIfStudentIdAndInternshipProcessMatchesOrThrowException(Integer userId, Integer processId) {
        if (!userId.equals(processId)) {
            logger.error("The internshipProcess id given does not belong to the student. Student id: "
                    + userId);
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
    }

    private void checkIfProcessStatusMatchesOrThrowException(ProcessStatusEnum expectedStatus,
                                                             ProcessStatusEnum processStatus) {
        if (expectedStatus != processStatus) {
            logger.info("Process status is not matched with the expected status. Process status: " + processStatus
                    + " Expected status: " + expectedStatus);
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
    }

    private List<ProcessAssignee> prepareProcessAssigneeList(InternshipProcess internshipProcess, Date now) {
        List<Integer> assigneeIdList = findAssigneeIdList(internshipProcess.getProcessStatus(),
                internshipProcess.getDepartment(),
                internshipProcess.getStudent().getId());
        if (assigneeIdList == null || assigneeIdList.isEmpty()) {
            logger.info("There is no assignee found to continues the process.");
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }

        ProcessAssignee baseProcessAssignee = new ProcessAssignee();
        baseProcessAssignee.setLogDates(LogDates.builder().createDate(now).updateDate(now).build());
        baseProcessAssignee.setProcessId(internshipProcess.getId());

        List<ProcessAssignee> processAssignees = new ArrayList<>();
        for (Integer assigneeId : assigneeIdList) {
            ProcessAssignee processAssignee = new ProcessAssignee();
            BeanUtils.copyProperties(baseProcessAssignee, processAssignee);
            processAssignee.setAssigneeId(assigneeId);
            processAssignees.add(processAssignee);
        }
        return processAssignees;
    }

    private List<Integer> findAssigneeIdList(ProcessStatusEnum processStatusEnum, Department department, Integer studentId) {
        return switch (processStatusEnum) {
            case FORM -> academicianService.findAcademicianIdsByInternshipCommitteeAndDepartment(true, department.getId());
            case PRE1 -> academicianService.findAcademicianIdsByDepartmentChairAndDepartment(true, department.getId());
            case PRE2 -> academicianService.findAcademicianIdsByExecutiveAndDepartment(true, department.getId());
            case PRE3 -> academicianService.findAcademicianIdsByAcademicAndDepartment(true, department.getId());
            case PRE4 -> {
                List<Integer> assigneIdList = new ArrayList<>();
                assigneIdList.add(studentId);
                yield assigneIdList;
            }
            default -> throw new IllegalStateException("Unexpected value: " + processStatusEnum);
        };
    }

    @Transactional
    public void insertProcessAssigneesAndUpdateProcessStatus(List<ProcessAssignee> processAssigneeList,
                                                             InternshipProcess internshipProcess) {
        processAssigneeService.deleteByProcessId(internshipProcess.getId());
        processAssigneeService.saveAll(processAssigneeList);
        internshipProcessDao.save(internshipProcess);
    }

    private boolean areFormFieldsEntered(InternshipProcess internshipProcess) {
        return !(internshipProcess.getTc() == null || internshipProcess.getStudentNumber() == null ||
                internshipProcess.getTelephoneNumber() == null || internshipProcess.getClassNumber() == null ||
                internshipProcess.getPosition() == null || internshipProcess.getInternshipType() == null ||
                internshipProcess.getInternshipNumber() == null || internshipProcess.getStartDate() == null ||
                internshipProcess.getEndDate() == null || internshipProcess.getCompany() == null ||
                internshipProcess.getDepartment() == null || internshipProcess.getEngineerMail() == null ||
                internshipProcess.getEngineerName() == null || internshipProcess.getChoiceReason() == null ||
                internshipProcess.getSgkEntry() == null || internshipProcess.getGssEntry() == null ||
                internshipProcess.getMustehaklikBelgesiPath() == null || internshipProcess.getStajYeriFormuPath() == null);
    }

    private boolean isNextStatusEditable(ProcessStatusEnum nextStatus) {
        return nextStatus == ProcessStatusEnum.FORM || nextStatus == ProcessStatusEnum.REJECTED;
    }

    private void copyDtoToEntity(InternshipProcess internshipProcess, InternshipProcessUpdateRequest internshipProcessUpdateRequest, Department department, Company company) {
        Date now = new Date();

        BeanUtils.copyProperties(internshipProcessUpdateRequest, internshipProcess);
        internshipProcess.setLogDates(LogDates.builder().createDate(now).updateDate(now).build());
        internshipProcess.setCompany(company);
        internshipProcess.setDepartment(department);
        internshipProcess.setProcessStatus(ProcessStatusEnum.FORM);
    }

    private void copyEntityToDto(InternshipProcess internshipProcess, InternshipProcessGetResponse internshipProcessGetResponse) {
        BeanUtils.copyProperties(internshipProcess, internshipProcessGetResponse);
        internshipProcessGetResponse.setCompanyId(internshipProcess.getCompany().getId());
        internshipProcessGetResponse.setDepartmentId(internshipProcess.getDepartment().getId());
    }

}
