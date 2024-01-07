package com.teamaloha.internshipprocessmanagement.service;

import com.teamaloha.internshipprocessmanagement.dao.InternshipProcessDao;
import com.teamaloha.internshipprocessmanagement.dto.InternshipProcess.*;
import com.teamaloha.internshipprocessmanagement.dto.SearchByPageDto;
import com.teamaloha.internshipprocessmanagement.dto.SearchCriteria;
import com.teamaloha.internshipprocessmanagement.dto.SearchDto;
import com.teamaloha.internshipprocessmanagement.dto.academician.AcademicsGetStudentAllProcessResponse;
import com.teamaloha.internshipprocessmanagement.dto.doneInternshipProcess.DoneInternshipProcessGetResponse;
import com.teamaloha.internshipprocessmanagement.dto.holiday.IsValidRangeRequest;
import com.teamaloha.internshipprocessmanagement.entity.*;
import com.teamaloha.internshipprocessmanagement.entity.embeddable.LogDates;
import com.teamaloha.internshipprocessmanagement.enums.ErrorCodeEnum;
import com.teamaloha.internshipprocessmanagement.enums.ProcessOperationType;
import com.teamaloha.internshipprocessmanagement.enums.ProcessStatusEnum;
import com.teamaloha.internshipprocessmanagement.exceptions.CustomException;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class InternshipProcessService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final InternshipProcessDao internshipProcessDao;
    private final DepartmentService departmentService;
    private final CompanyService companyService;
    private final AcademicianService academicianService;
    private final HolidayService holidayService;
    private final MailService mailService;
    private final ProcessOperationService processOperationService;
    private final ProcessAssigneeService processAssigneeService;
    private InternshipProcessService self;
    private final ApplicationContext applicationContext;
    private final FiltersSpecification<InternshipProcess> filtersSpecification;
    private final DoneInternshipProcessService doneInternshipProcessService;

    @Autowired
    public InternshipProcessService(InternshipProcessDao internshipProcessDao, DepartmentService departmentService,
                                    CompanyService companyService, AcademicianService academicianService,
                                    HolidayService holidayService, ProcessAssigneeService processAssigneeService,
                                    ApplicationContext applicationContext,
                                    FiltersSpecification<InternshipProcess> filtersSpecification,
                                    MailService mailService, DoneInternshipProcessService doneInternshipProcessService,
                                    ProcessOperationService processOperationService) {
        this.internshipProcessDao = internshipProcessDao;
        this.departmentService = departmentService;
        this.companyService = companyService;
        this.academicianService = academicianService;
        this.holidayService = holidayService;
        this.processAssigneeService = processAssigneeService;
        this.applicationContext = applicationContext;
        this.filtersSpecification = filtersSpecification;
        this.mailService = mailService;
        this.doneInternshipProcessService = doneInternshipProcessService;
        this.processOperationService = processOperationService;
    }

    @PostConstruct
    private void init() {
        self = applicationContext.getBean(InternshipProcessService.class);
    }

//    @EventListener(ApplicationReadyEvent.class)
//    public void afterStartup() {
//        internshipProcessDao.updateNullRejectedFields();
//    }

    public InternshipProcessInitResponse initInternshipProcess(Integer studentId) {
        // Only setting the ID of Student entity is enough to insert InternshipProcess entity.
        Student student = new Student();
        student.setId(studentId);

        // Check if there is more than 2 active process
        Integer maxProcessNumber = 2;
        Integer count = internshipProcessDao.countByStudentId(studentId);
        Integer doneCount = doneInternshipProcessService.countDoneInternshipProcessByStudentIdAndProcessStatus(studentId, ProcessStatusEnum.DONE);
        if (count >= maxProcessNumber) {
            logger.error("Process cannot creatable for this student (2 or more active process). Student id: " + studentId);
            throw new CustomException(ErrorCodeEnum.INTERNSHIP_MAX_NUMBER.getErrorCode(), HttpStatus.BAD_REQUEST);
        }

        Date now = new Date();
        InternshipProcess emptyProcess = new InternshipProcess();
        emptyProcess.setStudent(student);
        emptyProcess.setLogDates(LogDates.builder().createDate(now).updateDate(now).build());
        emptyProcess.setProcessStatus(ProcessStatusEnum.FORM);
        emptyProcess.setEditable(true);
        emptyProcess.setRejected(false);
        InternshipProcess savedProcess = internshipProcessDao.save(emptyProcess);

        logger.info("Created InternshipProcess with ID: " + savedProcess.getId());
        return new InternshipProcessInitResponse(savedProcess.getId());
    }

    public InternshipProcessGetAllResponse getAllInternshipProcess(Integer studentId) {
        return getAllInternshipProcess(studentId, null, false);
    }

    public InternshipProcessGetAllResponse getAllInternshipProcess(Integer studentId, ProcessStatusEnum processStatus,
                                                                   boolean academicianResponse) {
        Student student = new Student();
        student.setId(studentId);
        List<InternshipProcess> internshipProcessList = null;

        if (processStatus != null) {
            internshipProcessList = internshipProcessDao.findAllByStudentAndProcessStatusNot(student, processStatus);
        }
        else {
            internshipProcessList = internshipProcessDao.findAllByStudent(student);
        }

        return createInternshipProcessGetAllResponse(
                internshipProcessList,
                doneInternshipProcessService.getAllDoneInternshipProcess(studentId).getInternshipProcessList(),
                academicianResponse);
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

    public AcademicsGetStudentAllProcessResponse getStudentAllProcess(Integer studentId, Integer academicianId) {
        academicianService.getAcademicianIfExistsOrThrowException(academicianId);
        List<InternshipProcessGetResponse> processList = getAllInternshipProcess(studentId, ProcessStatusEnum.FORM, true).getInternshipProcessList();

        return new AcademicsGetStudentAllProcessResponse(processList);
    }

    public AcademicsGetStudentAllProcessResponse getAllActiveProcesses() {
        List<InternshipProcess> processList = internshipProcessDao.findAllByProcessStatusIn(List.of(ProcessStatusEnum.IN1, ProcessStatusEnum.IN2));
        List<InternshipProcessGetResponse> processGetResponseList = createInternshipProcessGetAllResponse(processList, true).getInternshipProcessList();

        return new AcademicsGetStudentAllProcessResponse(processGetResponseList);
    }

    public InternshipProcessGetAllResponse getAllInternshipProcessByCompany(Integer companyId) {
        List<InternshipProcess> internshipProcessList = internshipProcessDao.findAllByCompany_Id(companyId);
        return createInternshipProcessGetAllResponse(internshipProcessList, true);
    }

    public InternshipProcessGetAllResponse getAssignedInternshipProcess(Integer assigneeId,
                                                                        InternshipProcessSearchDto internshipProcessSearchDto) {
        List<InternshipProcess> internshipProcessList = internshipProcessDao.findAll(prepereInternshipProcessSearchSpecification(assigneeId, internshipProcessSearchDto),
                SearchByPageDto.getPageable(internshipProcessSearchDto.getSearchByPageDto())).toList();
        return createInternshipProcessGetAllResponse(internshipProcessList, true);
    }

    public InternshipProcessGetAllResponse getAssignedInternshipProcess(Integer assigneeId) {
        // get all processes that assigned to the given assignee
        List<Integer> processIdList = processAssigneeService.findAllProcessIdByAssigneeId(assigneeId);
        // TODO: Bu kisimda assigneeId'ye gore processleri getirirken, process statusu da kontrol edilecek.
        List<InternshipProcess> internshipProcessList = internshipProcessDao.findAllById(processIdList);
        return createInternshipProcessGetAllResponse(internshipProcessList, true);
    }

    private InternshipProcessGetAllResponse createInternshipProcessGetAllResponse(List<InternshipProcess> internshipProcessList, boolean academicianResponse) {
        return createInternshipProcessGetAllResponse(internshipProcessList, null, academicianResponse);
    }

    private InternshipProcessGetAllResponse createInternshipProcessGetAllResponse(List<InternshipProcess> internshipProcessList,
                                                                                  List<DoneInternshipProcessGetResponse> doneInternshipProcessList,
                                                                                  boolean academicianResponse) {
        List<InternshipProcessGetResponse> internshipProcessGetResponseList = new ArrayList<>();
        for (InternshipProcess internshipProcess : internshipProcessList) {
            InternshipProcessGetResponse internshipProcessGetResponse = academicianResponse ? new InternshipProcessAcademicianGetResponse() : new InternshipProcessGetResponse();
            copyEntityToDto(internshipProcess, internshipProcessGetResponse);
            internshipProcessGetResponseList.add(internshipProcessGetResponse);
        }
        if (doneInternshipProcessList != null) {
            internshipProcessGetResponseList.addAll(doneInternshipProcessList);
        }
        return new InternshipProcessGetAllResponse(internshipProcessGetResponseList);
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

    public void sendReport(SendReportRequest loadReportRequest, Integer studentId) {
        Integer processId = loadReportRequest.getId();

        InternshipProcess internshipProcess = getInternshipProcessIfExistsOrThrowException(processId);
        ProcessOperation processOperation = null;
        ProcessStatusEnum oldStatus = internshipProcess.getProcessStatus();

        // Check if internship process is in POST status
        checkIfProcessStatusesMatchesOrThrowException(List.of(ProcessStatusEnum.POST),
                internshipProcess.getProcessStatus());

        checkIfStudentIdAndInternshipProcessMatchesOrThrowException(studentId, internshipProcess.getStudent().getId());

        /*internshipProcess.setStajRaporuPath(loadReportRequest.getStajRaporuPath());*/
        internshipProcess.setReportLastEditDate(null);
        internshipProcess.setAssignerId(studentId);

        Date now = new Date();
        List<ProcessAssignee> assigneeList = prepareProcessAssigneeList(internshipProcess, now);
        internshipProcess.setProcessStatus(ProcessStatusEnum.REPORT1);
        internshipProcess.setRejected(false);

        InternshipProcess updatedInternshipProcess = internshipProcessDao.save(internshipProcess);

        processOperation = prepareProcessOperation(internshipProcess, oldStatus, ProcessOperationType.SUBMIT, null, now);
        self.insertProcessAssigneesAndUpdateProcessStatus(assigneeList, processOperation, updatedInternshipProcess);

        logger.info("Report sent for InternshipProcess with ID: " + updatedInternshipProcess.getId());
    }

    // TODO: Ne zaman silebilir? Ornegin staj sureci baslamissa silinemez gibi bir kontrol eklenebilir.
    public void deleteInternshipProcess(Integer processId) {

        InternshipProcess internshipProcess = internshipProcessDao.findInternshipProcessById(processId);
        if (internshipProcess.getEditable()) {
            // Delete assignees
            processAssigneeService.deleteByProcess(internshipProcess);

            internshipProcessDao.deleteById(processId);
            logger.info("Deleted InternshipProcess with ID: " + processId);
        } else {
            logger.error("InternshipProcess not deletable. InternshipProcess ID:" + processId);
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
    }

    public void internshipCancellationRequest(Integer internshipProcessID, Integer studentId) {
        Integer dayNumber = 3;
        Date now = new Date();

        InternshipProcess internshipProcess = checkIfRequestIsPossible(internshipProcessID, studentId, dayNumber);
        ProcessOperation processOperation = null;
        ProcessStatusEnum oldStatus = internshipProcess.getProcessStatus();

        // Find assigneee list and set process status to CANCEL
        List<ProcessAssignee> assigneeList = prepareProcessAssigneeList(internshipProcess, now);
        internshipProcess.setProcessStatus(ProcessStatusEnum.CANCEL);
        internshipProcess.getLogDates().setUpdateDate(now);
        internshipProcess.setAssignerId(studentId);

        // Save the process with Assignee list
        processOperation = prepareProcessOperation(internshipProcess, oldStatus, ProcessOperationType.SUBMIT, null, now);
        self.insertProcessAssigneesAndUpdateProcessStatus(assigneeList, processOperation, internshipProcess);
    }

    public void internshipExtensionRequest(InternshipExtensionRequestDto internshipExtensionRequestDto, Integer userId) {
        Integer dayNumber = 7;
        Date now = new Date();
        InternshipProcess internshipProcess = checkIfRequestIsPossible(internshipExtensionRequestDto.getProcessId(), userId, dayNumber);
        ProcessOperation processOperation = null;
        ProcessStatusEnum oldStatus = internshipProcess.getProcessStatus();

        checkIfIsGivenWorkDayTrueOrThrowException(internshipProcess, internshipExtensionRequestDto);

        // Find assignee list and set process status to EXTEND
        List<ProcessAssignee> assigneeList = prepareProcessAssigneeList(internshipProcess, now);
        internshipProcess.setProcessStatus(ProcessStatusEnum.EXTEND);
        internshipProcess.setRequestedEndDate(internshipExtensionRequestDto.getRequestDate());
        internshipProcess.setAssignerId(userId);
        internshipProcess.getLogDates().setUpdateDate(now);

        // Save the process with Assignee list
        processOperation = prepareProcessOperation(internshipProcess, oldStatus, ProcessOperationType.SUBMIT, null, now);
        self.insertProcessAssigneesAndUpdateProcessStatus(assigneeList, processOperation, internshipProcess);
    }

    public void startInternshipApprovalProcess(Integer processId, Integer studentId) {
        InternshipProcess internshipProcess = getInternshipProcessIfExistsOrThrowException(processId);
        checkIfStudentIdAndInternshipProcessMatchesOrThrowException(studentId, internshipProcess.getStudent().getId());

        // If process is rejected, then pre statuses are accepted. Otherwise, only form is accepted.
        ArrayList<ProcessStatusEnum> expectedStatuses = new ArrayList<>();
        expectedStatuses.add(ProcessStatusEnum.FORM);
        if (internshipProcess.getRejected()) {
            expectedStatuses.add(ProcessStatusEnum.PRE1);
            expectedStatuses.add(ProcessStatusEnum.PRE2);
            expectedStatuses.add(ProcessStatusEnum.PRE3);
            expectedStatuses.add(ProcessStatusEnum.PRE4);
        }
        checkIfProcessStatusesMatchesOrThrowException(expectedStatuses, internshipProcess.getProcessStatus());

        if (!areFormFieldsEntered(internshipProcess)) {
            logger.error("The form fields are not completed properly to start internship approval process.");
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }

        Date now = new Date();
        ProcessOperation processOperation = null;
        ProcessStatusEnum oldStatus = internshipProcess.getProcessStatus();

        // Assign the process to assignees
        List<ProcessAssignee> assigneeList = prepareProcessAssigneeList(internshipProcess, now);

        // Set updated process status field
        ProcessStatusEnum nextStatus = ProcessStatusEnum.PRE1;
        internshipProcess.setProcessStatus(nextStatus);
        internshipProcess.setEditable(false);
        internshipProcess.setRejected(false);
        internshipProcess.setAssignerId(studentId);
        internshipProcess.getLogDates().setUpdateDate(now);

        processOperation = prepareProcessOperation(internshipProcess, oldStatus, ProcessOperationType.SUBMIT, null, now);
        self.insertProcessAssigneesAndUpdateProcessStatus(assigneeList, processOperation, internshipProcess);

        List<Integer> assigneeIds = assigneeList.stream().map(ProcessAssignee::getAssigneeId).toList();
        List<String> to = academicianService.getAcademiciansMail(assigneeIds);

        mailService.sendMail(
                to,
                null,
                "Staj Başvurusu",
                "Staj Başvurusu Yapıldı bu link üzerinden detayları inceleyebilirsiniz." +
                        "http://localhost:3000/internship-process/" + internshipProcess.getId()
        );
    }

    public void submitInternshipInfo(InternshipInfoSubmitRequest internshipInfoSubmitRequest, Integer studentId) {
        Integer processId = internshipInfoSubmitRequest.getId();

        InternshipProcess internshipProcess = getInternshipProcessIfExistsOrThrowException(processId);

        checkIfProcessStatusesMatchesOrThrowException(List.of(ProcessStatusEnum.IN1, ProcessStatusEnum.IN2),
                internshipProcess.getProcessStatus());

        checkIfStudentIdAndInternshipProcessMatchesOrThrowException(studentId, internshipProcess.getStudent().getId());

        internshipProcess.setEngineerMail(internshipInfoSubmitRequest.getEngineerMail());
        internshipProcess.setEngineerName(internshipInfoSubmitRequest.getEngineerName());
        internshipProcess.setPosition(internshipInfoSubmitRequest.getPosition());
        InternshipProcess updatedInternshipProcess = internshipProcessDao.save(internshipProcess);

        logger.info("Information added to InternshipProcess with ID: " + updatedInternshipProcess.getId());
    }

    public void evaluateInternshipProcess(InternshipProcessEvaluateRequest internshipProcessEvaluateRequest) {
        Boolean savedAsDone = false;
        Integer processId = internshipProcessEvaluateRequest.getProcessId();
        Integer academicianId = internshipProcessEvaluateRequest.getAcademicianId();
        Boolean edit = internshipProcessEvaluateRequest.getReportEditRequest();
        ProcessOperationType processOperationType = null;

        // If the request is edit request or rejection request, check if the comment is given
        if ((!internshipProcessEvaluateRequest.getApprove() || (edit != null && edit)) &&
                StringUtils.isBlank(internshipProcessEvaluateRequest.getComment())) {
            logger.error("Rejection or Edit Request without comment. AcademicianId: " + academicianId +
                    " ProcessId: " + processId);
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }

        boolean isAssignee = processAssigneeService.existsByProcessIdAndAssigneeId(processId, academicianId);
        if (!isAssignee) {
            logger.error("Internship process evaluation tried without being a real assignee for the process.");
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }

        Date now = new Date();
        InternshipProcess internshipProcess = getInternshipProcessIfExistsOrThrowException(processId);
        internshipProcess.setComment(internshipProcessEvaluateRequest.getComment());
        internshipProcess.getLogDates().setUpdateDate(now);
        internshipProcess.setAssignerId(academicianId);
        internshipProcess.setCommentOwner(academicianService.getAcademicianNameById(academicianId));

        ProcessOperation processOperation = null;
        ProcessStatusEnum oldStatus = internshipProcess.getProcessStatus();

        ProcessStatusEnum nextStatus = null;
        List<ProcessAssignee> assigneeList = null;
        if ((edit != null && edit)) {
            // Edit request for report

            if(internshipProcessEvaluateRequest.getReportEditDays() == null || internshipProcessEvaluateRequest.getReportEditDays() <= 0) {
                logger.error("Report edit request without given day number. AcademicianId: " + academicianId +
                        " ProcessId: " + processId);
                throw new CustomException(HttpStatus.BAD_REQUEST);
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(now);
            calendar.add(Calendar.DAY_OF_MONTH, internshipProcessEvaluateRequest.getReportEditDays());
            if (internshipProcess.getProcessStatus() != ProcessStatusEnum.REPORT1 &&
                    internshipProcess.getProcessStatus() != ProcessStatusEnum.REPORT2) {
                logger.error("Report edit request is not possible for this process status. Process status: "
                        + internshipProcess.getProcessStatus());
                throw new CustomException(HttpStatus.BAD_REQUEST);
            }
            mailService.sendMail(
                    List.of(internshipProcess.getStudent().getMail()),
                    null,
                    "Staj Raporu Düzenleme İsteği",
                    "Staj Raporunuzun düzenlenmesi için istekte bulunuldu. Bu link üzerinden detayları inceleyebilirsiniz." +
                            "http://localhost:3000/internship-process/" + internshipProcess.getId());
            assigneeList = new ArrayList<>();
            internshipProcess.setReportLastEditDate(calendar.getTime());
            internshipProcess.setRejected(true);
            nextStatus = ProcessStatusEnum.POST;
            processOperationType = ProcessOperationType.REJECTION;
        } else {
            if (!internshipProcessEvaluateRequest.getApprove()) {
                // Rejection
                if (internshipProcess.getProcessStatus() == ProcessStatusEnum.REPORT1) {
                    logger.error("Research assistants can only request edits. intenshipProcess Id: "
                            + internshipProcess.getId());
                    throw new CustomException(HttpStatus.BAD_REQUEST);
                }

                assigneeList = new ArrayList<>();

                if (internshipProcess.getProcessStatus() == ProcessStatusEnum.CANCEL) {
                    nextStatus = ProcessStatusEnum.IN1;
                } else if (internshipProcess.getProcessStatus() == ProcessStatusEnum.EXTEND) {
                    internshipProcess.setRequestedEndDate(null);
                    nextStatus = ProcessStatusEnum.IN1;
                } else if (internshipProcess.getProcessStatus() == ProcessStatusEnum.REPORT2) {
                    internshipProcess.setProcessStatus(ProcessStatusEnum.FAIL);
                    processOperation = prepareProcessOperation(internshipProcess, oldStatus, ProcessOperationType.REJECTION,
                            internshipProcessEvaluateRequest.getComment(), now);
                    self.saveAsDoneInternshipProcess(internshipProcess, processOperation, ProcessStatusEnum.FAIL);
                    savedAsDone = true;
                } else {
                    internshipProcess.setRejected(true);
                    internshipProcess.setEditable(true);
                    nextStatus = internshipProcess.getProcessStatus();
                }
                processOperationType = ProcessOperationType.REJECTION;
                mailService.sendMail(
                        List.of(internshipProcess.getStudent().getMail()),
                        null,
                        "Staj Başvurusu Reddedildi",
                        "Staj Başvurunuz reddedildi. Bu link üzerinden detayları inceleyebilirsiniz." +
                                "http://localhost:3000/internship-process/" + internshipProcess.getId());
            } else {
                // Approval
                assigneeList = prepareProcessAssigneeList(internshipProcess, now);
                if (internshipProcess.getProcessStatus() == ProcessStatusEnum.CANCEL) {
                    // If the process is cancelled, save as done saveAsDoneInternshipProcess with CANCEL status
                    internshipProcess.setProcessStatus(ProcessStatusEnum.CANCEL);
                    processOperation = prepareProcessOperation(internshipProcess, oldStatus, ProcessOperationType.APPROVAL,
                            internshipProcessEvaluateRequest.getComment(), now);
                    self.saveAsDoneInternshipProcess(internshipProcess, processOperation, ProcessStatusEnum.CANCEL);
                    savedAsDone = true;
                } else if (internshipProcess.getProcessStatus() == ProcessStatusEnum.EXTEND) {
                    internshipProcess.setEndDate(internshipProcess.getRequestedEndDate());
                    internshipProcess.setRequestedEndDate(null);
                    nextStatus = ProcessStatusEnum.IN1;
                } else {
                    // If the process is Done, save it as DoneInternshipProcess
                    if (internshipProcess.getProcessStatus() == ProcessStatusEnum.REPORT2) {
                        internshipProcess.setProcessStatus(ProcessStatusEnum.DONE);
                        processOperation = prepareProcessOperation(internshipProcess, oldStatus, ProcessOperationType.APPROVAL,
                                internshipProcessEvaluateRequest.getComment(), now);
                        self.saveAsDoneInternshipProcess(internshipProcess, processOperation, ProcessStatusEnum.DONE);
                        savedAsDone = true;
                    }
                    nextStatus = ProcessStatusEnum.findNextStatus(internshipProcess.getProcessStatus());
                }
                processOperationType = ProcessOperationType.APPROVAL;
                mailService.sendMail(
                        List.of(internshipProcess.getStudent().getMail()),
                        null,
                        "Staj Başvurusu Onaylandı",
                        "Staj Başvurunuz onaylandı. Bu link üzerinden detayları inceleyebilirsiniz." +
                                "http://localhost:3000/internship-process/" + internshipProcess.getId());
            }
        }

        // If the process is not cancelled, update the process status
        if (!savedAsDone) {
            processOperation = prepareProcessOperation(internshipProcess, oldStatus, processOperationType,
                    internshipProcessEvaluateRequest.getComment(), now);
            internshipProcess.setProcessStatus(nextStatus);
            self.insertProcessAssigneesAndUpdateProcessStatus(assigneeList, processOperation, internshipProcess);
        }
    }

    private ProcessOperation prepareProcessOperation(InternshipProcess internshipProcess, ProcessStatusEnum oldStatus,
                                         ProcessOperationType processOperationType, String comment,
                                         Date now) {
        // Add Process operation to DB as ProcessOperation
        ProcessOperation processOperation = new ProcessOperation();
        processOperation.setProcessId(internshipProcess.getId());
        processOperation.setUserId(internshipProcess.getAssignerId());
        processOperation.setOldStatus(oldStatus);
        processOperation.setNewStatus(internshipProcess.getProcessStatus());
        processOperation.setOperationType(processOperationType);
        processOperation.setComment(comment);
        processOperation.setLogDates(LogDates.builder().createDate(now).updateDate(now).build());
        return processOperation;
    }

    @Transactional
    public void saveAsDoneInternshipProcess(InternshipProcess internshipProcess, ProcessOperation processOperation,
                                            ProcessStatusEnum processStatus) {
        // Delete assignees
        processAssigneeService.deleteByProcess(internshipProcess);

        // Save as DoneInternshipProcess and delete from InternshipProcess
        DoneInternshipProcess doneInternshipProcess = new DoneInternshipProcess();
        BeanUtils.copyProperties(internshipProcess, doneInternshipProcess);
        doneInternshipProcess.setStudent(internshipProcess.getStudent());
        doneInternshipProcess.setLogDates(LogDates.builder().createDate(new Date()).updateDate(new Date()).build());
        doneInternshipProcess.setProcessStatus(processStatus);
        internshipProcessDao.delete(internshipProcess);
        doneInternshipProcessService.save(doneInternshipProcess);
        processOperationService.save(processOperation);
    }

    public void checkReportEditLastDates() {
        Date now = new Date();
        List<InternshipProcess> internshipProcessList = internshipProcessDao.findAllByProcessStatus(ProcessStatusEnum.POST);
        for (InternshipProcess internshipProcess : internshipProcessList) {
            Date reportLastEditDate = internshipProcess.getReportLastEditDate();
            if (reportLastEditDate != null && reportLastEditDate.after(now)) {
                internshipProcess.setReportLastEditDate(null);
                internshipProcess.setProcessStatus(ProcessStatusEnum.REPORT1);
                internshipProcessDao.save(internshipProcess);
            }
        }
    }

    public void finishInternshipProcesses() {
        Date now = new Date();
        List<InternshipProcess> internshipProcessList = internshipProcessDao.findAllByProcessStatus(ProcessStatusEnum.IN1);
        for (InternshipProcess internshipProcess : internshipProcessList) {
            if (internshipProcess.getEndDate().before(now)) {
                internshipProcess.setProcessStatus(ProcessStatusEnum.POST);
                internshipProcessDao.save(internshipProcess);
            }
        }
    }

    public void activateInternshipProcesses() {
        Date now = new Date();
        List<InternshipProcess> internshipProcessList = internshipProcessDao.findAllByProcessStatus(ProcessStatusEnum.values()[(ProcessStatusEnum.IN1.ordinal()) - 1]);
        for (InternshipProcess internshipProcess : internshipProcessList) {
            if (internshipProcess.getStartDate().before(now)) {
                internshipProcess.setProcessStatus(ProcessStatusEnum.IN1);
                internshipProcessDao.save(internshipProcess);
            }
        }
    }

    public void checkReportSubmitLastDates() {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        List<InternshipProcess> internshipProcessList = internshipProcessDao.findAllByProcessStatus(ProcessStatusEnum.POST);
        for (InternshipProcess internshipProcess : internshipProcessList) {
            calendar.setTime(internshipProcess.getEndDate());
            calendar.add(Calendar.DAY_OF_MONTH, 7);
            if (calendar.getTime().before(now)) {
                internshipProcess.setProcessStatus(ProcessStatusEnum.FAIL);
                internshipProcessDao.save(internshipProcess);
            }
        }
    }

    public void remindToEnterEngineerInfo() {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        List<InternshipProcess> internshipProcessList = internshipProcessDao.findAllByProcessStatus(ProcessStatusEnum.IN1);
        for (InternshipProcess internshipProcess : internshipProcessList) {
            calendar.setTime(internshipProcess.getStartDate());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            if (calendar.getTime().before(now)) {
                mailService.sendMail(
                        List.of(internshipProcess.getStudent().getMail()),
                        null,
                        "Girilmesi Gereken Ek Staj Bilgileri",
                        "Sevgili Öğrencimiz,\n" +
                                "\n  Stajınızın ilk haftası içinde sizi denetleyen mühendisin iletişim bilgilerini ve " +
                                "stajını gerçekleştirmekte olduğunuz pozisyon bilgisini girmeniz gerekmektedir. " +
                                "Bu link üzerinden detayları inceleyebilirsiniz. http://localhost:3000/internship-process/" + internshipProcess.getId()
                );
            }
            calendar.add(Calendar.DAY_OF_MONTH, 4);
            if (calendar.getTime().before(now)) {
                mailService.sendMail(
                        List.of(internshipProcess.getStudent().getMail()),
                        null,
                        "Girilmesi Gereken Ek Staj Bilgileri İçin Son Gün",
                        "Sevgili Öğrencimiz,\n" +
                                "\n  Yarın sizi denetleyen mühendisin iletişim bilgilerini stajını gerçekleştirmekte olduğunuz " +
                                "pozisyon bilgisini girmeniz için verilen sürenin son günü olduğunu hatırlatır iyi günler dileriz. " +
                                "Bu link üzerinden detayları inceleyebilirsiniz. http://localhost:3000/internship-process/" + internshipProcess.getId()
                );
            }
        }
    }

    public InternshipProcess getInternshipProcessIfExistsOrThrowException(Integer processId) {
        InternshipProcess internshipProcess = internshipProcessDao.findInternshipProcessById(processId);
        if (internshipProcess == null) {
            logger.error("InternshipProcess with ID " + processId + " not found for update.");
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
        return internshipProcess;
    }

    private InternshipProcess checkIfRequestIsPossible(Integer processID, Integer userId, Integer dayNumber) {
        InternshipProcess internshipProcess = getInternshipProcessIfExistsOrThrowException(processID);
        Date now = new Date();

        checkIfStudentIdAndInternshipProcessMatchesOrThrowException(userId, internshipProcess.getStudent().getId());

        // Check if the 7 days passed from the internship start date
        checkIfDiffSmallerOrThrowException(internshipProcess.getStartDate(), now, dayNumber);

        // Check if the process is approved (in IN1 OR IN2 status)
        checkIfProcessStatusesMatchesOrThrowException(List.of(ProcessStatusEnum.IN1, ProcessStatusEnum.IN2),
                internshipProcess.getProcessStatus());


        return internshipProcess;
    }

    private void checkIfIsGivenWorkDayTrueOrThrowException(InternshipProcess internshipProcess, InternshipExtensionRequestDto internshipExtensionRequestDto) {
        if (holidayService.isGivenWorkDayTrue(new IsValidRangeRequest(internshipProcess.getEndDate(),
                internshipExtensionRequestDto.getRequestDate(), internshipExtensionRequestDto.getExtensionDayNumber()))) {
            logger.error("The number of extension date and end date are not matching. Normal end Date: " +
                    internshipProcess.getEndDate() + " Requested End Date" + internshipExtensionRequestDto.getRequestDate() +
                    " Expected Number of Day: " + internshipExtensionRequestDto.getExtensionDayNumber());
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }

    }

    private void checkIfDiffSmallerOrThrowException(Date startDate, Date now, Integer dayNumber) {
        long diffInMillies = Math.abs(now.getTime() - startDate.getTime());
        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

        if (diff > dayNumber) {
            logger.error("The expected date is passed. Start Date: " + startDate + " End Date" + now + " Expected İnterval: " + dayNumber);
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
    }

    public void checkIfStudentIdAndInternshipProcessMatchesOrThrowException(Integer userId, Integer processId) {
        if (!userId.equals(processId)) {
            logger.error("The internshipProcess id given does not belong to the student. Student id: "
                    + userId);
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
    }

    private void checkIfProcessStatusesMatchesOrThrowException(List<ProcessStatusEnum> expectedStatuses,
                                                               ProcessStatusEnum processStatus) {
        if (!expectedStatuses.contains(processStatus)) {
            logger.info("Process status is not matched with the expected status. Process status: " + processStatus
                    + " Expected statuses: " + expectedStatuses);
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
        baseProcessAssignee.setInternshipProcess(internshipProcess);

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
            case FORM, REJECTED, IN1 ->
                    academicianService.findAcademiciansIdsByInternshipCommitteeAndDepartment(true, department.getId());
            case PRE1 -> academicianService.findAcademicianIdsByDepartmentChairAndDepartment(true, department.getId());
            case PRE2 -> academicianService.findAcademicianIdsByExecutiveAndDepartment(true, department.getId());
            case PRE3, REPORT1 -> academicianService.findAcademicianIdsByAcademicAndDepartment(true, department.getId());
            /*case PRE4 -> academicianService.findAcademicianIdsByDeanAndDepartment(true, department.getId());*/
            case POST ->
                    academicianService.findAcademicianIdsByResearchAssistantAndDepartment(true, department.getId());
            case PRE4, CANCEL, EXTEND, REPORT2 -> {
                List<Integer> assigneIdList = new ArrayList<>();
                assigneIdList.add(studentId);
                yield assigneIdList;
            }
            default -> throw new IllegalStateException("Unexpected value: " + processStatusEnum);
        };
    }

    @Transactional
    public void insertProcessAssigneesAndUpdateProcessStatus(List<ProcessAssignee> processAssigneeList,
                                                             ProcessOperation processOperation,
                                                             InternshipProcess internshipProcess) {
        internshipProcessDao.save(internshipProcess);
        processOperationService.save(processOperation);
        processAssigneeService.deleteByProcess(internshipProcess);
        processAssigneeService.saveAll(processAssigneeList);
    }

    private boolean areFormFieldsEntered(InternshipProcess internshipProcess) {
        Set<String> excludedFields = new HashSet<>();
        excludedFields.add("assignerId");
        excludedFields.add("processAssignees");
        excludedFields.add("requestedEndDate");

        // TODO: BU ALANLAR KONTROL EDILECEK. SIMDILIK UPDATE'TE OLMADIGI ICIN SELIM'IN TESTLERI ICIN BOYLE YAPILDI.
        excludedFields.add("mufredatDurumuID");
        excludedFields.add("transkriptID");
        excludedFields.add("dersProgramiID");
        excludedFields.add("stajRaporuID");
        excludedFields.add("comment");
        excludedFields.add("reportLastEditDate");
        excludedFields.add("commentOwner");
        excludedFields.add("mustehaklikBelgesiID");
        excludedFields.add("stajYeriFormuID");

        Field[] fields = InternshipProcess.class.getDeclaredFields();
/*
        for (Field field : fields) {
            String fieldName = field.getName();
            if (!excludedFields.contains(fieldName)) {
                field.setAccessible(true);
                try {
                    if (field.get(internshipProcess) == null) {
                        logger.info("Field " + field.getName() + " is not entered.");
                        return false;  // If any non-excluded field is null, return false
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();  // Handle exception according to your needs
                    return false;
                }
            }
        }
       */
        return true;
    }

    private Specification<InternshipProcess> prepereInternshipProcessSearchSpecification(Integer assigneeId,
                                                                                         InternshipProcessSearchDto internshipProcessSearchDto) {
        Map<String, Comparable[]> criteriaMap = new HashMap<>();

        if (internshipProcessSearchDto.getStartDate() != null) {
            criteriaMap.put("startDate", new Comparable[]{internshipProcessSearchDto.getStartDate(), SearchCriteria.Operation.GREATER_THAN_OR_EQUAL_TO});
        }

        if (internshipProcessSearchDto.getEndDate() != null) {
            criteriaMap.put("endDate", new Comparable[]{internshipProcessSearchDto.getEndDate(), SearchCriteria.Operation.LESS_THAN_OR_EQUAL_TO});
        }

        if (internshipProcessSearchDto.getInternshipType() != null) {
            criteriaMap.put("internshipType", new Comparable[]{internshipProcessSearchDto.getInternshipType(), SearchCriteria.Operation.EQUAL});
        }

        if (internshipProcessSearchDto.getInternshipNumber() != null) {
            criteriaMap.put("internshipNumber", new Comparable[]{internshipProcessSearchDto.getInternshipNumber(), SearchCriteria.Operation.EQUAL});
        }

        if (internshipProcessSearchDto.getProcessStatusEnum() != null) {
            Comparable[] values = new Comparable[internshipProcessSearchDto.getProcessStatusEnum().size() + 1];
            values = internshipProcessSearchDto.getProcessStatusEnum().toArray(values);
            values[values.length - 1] = SearchCriteria.Operation.IN;
            criteriaMap.put("processStatus", values);
        }

        List<SearchCriteria> searchCriteriaList = filtersSpecification.convertMapToSearchCriteriaList(criteriaMap);

        // Write Join criteria separate to the dynamic SearchCriteria generator.
        if (assigneeId != null) {
            SearchCriteria criteriaJoin = new SearchCriteria();
            criteriaJoin.setJoinAttribute("processAssignees");
            String[] rootPathJoin = {"assigneeId"};
            criteriaJoin.setRootPath(rootPathJoin);
            Integer[] valuesJoin = {assigneeId};
            criteriaJoin.setValues(valuesJoin);
            criteriaJoin.setOperation(SearchCriteria.Operation.JOIN);
            searchCriteriaList.add(criteriaJoin);
        }

        return filtersSpecification.getSearchSpecification(searchCriteriaList, SearchDto.LogicOperator.AND);
    }

    private void copyDtoToEntity(InternshipProcess internshipProcess, InternshipProcessUpdateRequest internshipProcessUpdateRequest, Department department, Company company) {
        Date now = new Date();

        BeanUtils.copyProperties(internshipProcessUpdateRequest, internshipProcess);
        internshipProcess.setLogDates(LogDates.builder().createDate(now).updateDate(now).build());
        internshipProcess.setCompany(company);
        internshipProcess.setDepartment(department);
        internshipProcess.setProcessStatus(ProcessStatusEnum.FORM);
    }

    private void copyEntityToDto(InternshipProcess internshipProcess,
                                 InternshipProcessGetResponse internshipProcessGetResponse) {
        BeanUtils.copyProperties(internshipProcess, internshipProcessGetResponse);

        if (internshipProcess.getCompany() != null) {
            internshipProcessGetResponse.setCompanyId(internshipProcess.getCompany().getId());
            internshipProcessGetResponse.setCompanyName(internshipProcess.getCompany().getCompanyName());
        }

        if (internshipProcess.getDepartment() != null) {
            internshipProcessGetResponse.setDepartmentId(internshipProcess.getDepartment().getId());
        }

        internshipProcessGetResponse.setFullName(internshipProcess.getStudent().getFirstName() + " " + internshipProcess.getStudent().getLastName());
        internshipProcessGetResponse.setUpdateDate(internshipProcess.getLogDates().getUpdateDate());
        internshipProcessGetResponse.setStudentId(internshipProcess.getStudent().getId());
    }


    public void makePost(Integer internshipProcessID) {
        InternshipProcess internshipProcess = internshipProcessDao.findInternshipProcessById(internshipProcessID);
        internshipProcess.setProcessStatus(ProcessStatusEnum.POST);
        internshipProcessDao.save(internshipProcess);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Integer updateFileId(Integer processId, Integer newLocationId, String type) {
        InternshipProcess internshipProcess = internshipProcessDao.findInternshipProcessById(processId);
        Integer oldLocationId = null;

        if(type.equals("mufredatDurumuID")){
            oldLocationId = internshipProcess.getMufredatDurumuID();
            internshipProcess.setMufredatDurumuID(newLocationId);
        }
        else if(type.equals("transkriptID")){
            oldLocationId = internshipProcess.getTranskriptID();
            internshipProcess.setTranskriptID(newLocationId);
        }
        else if(type.equals("dersProgramıID")){
            oldLocationId = internshipProcess.getDersProgramiID();
            internshipProcess.setDersProgramiID(newLocationId);
        }
        else if(type.equals("stajRaporuID")){
            oldLocationId = internshipProcess.getStajRaporuID();
            internshipProcess.setStajRaporuID(newLocationId);
        }
        else if(type.equals("mustehaklikBelgesiID")){
            oldLocationId = internshipProcess.getMustehaklikBelgesiID();
            internshipProcess.setMustehaklikBelgesiID(newLocationId);
        }
        else if(type.equals("stajYeriFormuID")){
            oldLocationId = internshipProcess.getStajYeriFormuID();
            internshipProcess.setStajYeriFormuID(newLocationId);
        }

        internshipProcessDao.save(internshipProcess);
        return oldLocationId;
    }
}
