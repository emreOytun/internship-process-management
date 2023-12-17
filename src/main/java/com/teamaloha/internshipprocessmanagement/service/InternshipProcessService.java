package com.teamaloha.internshipprocessmanagement.service;

import com.teamaloha.internshipprocessmanagement.dao.InternshipProcessDao;
import com.teamaloha.internshipprocessmanagement.dto.InternshipProcess.*;
import com.teamaloha.internshipprocessmanagement.dto.SearchByPageDto;
import com.teamaloha.internshipprocessmanagement.dto.SearchCriteria;
import com.teamaloha.internshipprocessmanagement.dto.SearchDto;
import com.teamaloha.internshipprocessmanagement.dto.academician.AcademicsGetStudentAllProcessResponse;
import com.teamaloha.internshipprocessmanagement.dto.holiday.IsValidRangeRequest;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
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
    private final ProcessAssigneeService processAssigneeService;
    private InternshipProcessService self;
    private final ApplicationContext applicationContext;
    private final FiltersSpecification<InternshipProcess> filtersSpecification;

    @Autowired
    public InternshipProcessService(InternshipProcessDao internshipProcessDao, DepartmentService departmentService,
                                    CompanyService companyService, AcademicianService academicianService,
                                    HolidayService holidayService, ProcessAssigneeService processAssigneeService,
                                    ApplicationContext applicationContext,
                                    FiltersSpecification<InternshipProcess> filtersSpecification) {
        this.internshipProcessDao = internshipProcessDao;
        this.departmentService = departmentService;
        this.companyService = companyService;
        this.academicianService = academicianService;
        this.holidayService = holidayService;
        this.processAssigneeService = processAssigneeService;
        this.applicationContext = applicationContext;
        this.filtersSpecification = filtersSpecification;
    }

    @PostConstruct
    private void init() {
        self = applicationContext.getBean(InternshipProcessService.class);
    }


    public InternshipProcessInitResponse initInternshipProcess(Integer studentId) {
        // Only setting the ID of Student entity is enough to insert InternshipProcess entity.
        Student student = new Student();
        student.setId(studentId);

        // Check if there is more than 2 active process
        Integer count = internshipProcessDao.countByStudentId(studentId);
        if (count >= 2) {
            logger.error("Process cannot creatable for this student (2 or more active process). Student id: " + studentId);
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }

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

    public InternshipProcessGetAllResponse getAllInternshipProcess(Integer studentId) {
        Student student = new Student();
        student.setId(studentId);

        List<InternshipProcess> internshipProcessList = internshipProcessDao.findAllByStudent(student);
        return createInternshipProcessGetAllResponse(internshipProcessList);
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
        List<InternshipProcessGetResponse> processList = getAllInternshipProcess(studentId).getInternshipProcessList();


        return new AcademicsGetStudentAllProcessResponse(processList);
    }

    public InternshipProcessGetAllResponse getAssignedInternshipProcess(Integer assigneeId,
                                                                        InternshipProcessSearchDto internshipProcessSearchDto) {
        List<InternshipProcess> internshipProcessList = internshipProcessDao.findAll(prepereInternshipProcessSearchSpecification(assigneeId, internshipProcessSearchDto),
                SearchByPageDto.getPageable(internshipProcessSearchDto.getSearchByPageDto())).toList();
        return createInternshipProcessGetAllResponse(internshipProcessList);
    }

    public InternshipProcessGetAllResponse getAssignedInternshipProcess(Integer assigneeId) {
        // get all processes that assigned to the given assignee
        List<Integer> processIdList = processAssigneeService.findAllProcessIdByAssigneeId(assigneeId);
        // TODO: Bu kisimda assigneeId'ye gore processleri getirirken, process statusu da kontrol edilecek.
        List<InternshipProcess> internshipProcessList = internshipProcessDao.findAllById(processIdList);
        return createInternshipProcessGetAllResponse(internshipProcessList);
    }


    private InternshipProcessGetAllResponse createInternshipProcessGetAllResponse(List<InternshipProcess> internshipProcessList) {
        List<InternshipProcessGetResponse> internshipProcessGetResponseList = new ArrayList<>();
        for (InternshipProcess internshipProcess : internshipProcessList) {
            InternshipProcessGetResponse internshipProcessGetResponse = new InternshipProcessGetResponse();
            copyEntityToDto(internshipProcess, internshipProcessGetResponse);
            internshipProcessGetResponseList.add(internshipProcessGetResponse);
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
        Date now = new Date();
        Integer processId = loadReportRequest.getId();

        InternshipProcess internshipProcess = getInternshipProcessIfExistsOrThrowException(processId);

        // Check if internship process is in POST status
        checkIfProcessStatusesMatchesOrThrowException(List.of(ProcessStatusEnum.POST), internshipProcess.getProcessStatus());

        checkIfStudentIdAndInternshipProcessMatchesOrThrowException(studentId, internshipProcess.getStudent().getId());

        internshipProcess.setStajRaporuPath(loadReportRequest.getStajRaporuPath());

        List<ProcessAssignee> assigneeList = prepareProcessAssigneeList(internshipProcess, new Date());
        internshipProcess.setProcessStatus(ProcessStatusEnum.REPORT1);
        internshipProcess.getLogDates().setUpdateDate(now);

        InternshipProcess updatedInternshipProcess = internshipProcessDao.save(internshipProcess);

        self.insertProcessAssigneesAndUpdateProcessStatus(assigneeList, updatedInternshipProcess);

        logger.info("Report sent for InternshipProcess with ID: " + updatedInternshipProcess.getId());
    }

    // TODO: Ne zaman silebilir? Ornegin staj sureci baslamissa silinemez gibi bir kontrol eklenebilir.
    public void deleteInternshipProcess(Integer processId) {

        InternshipProcess internshipProcess = internshipProcessDao.findInternshipProcessById(processId);
        if (internshipProcess.getEditable()) {
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

        // Find assigneee list and set process status to CANCEL
        List<ProcessAssignee> assigneeList = prepareProcessAssigneeList(internshipProcess, now);
        internshipProcess.setProcessStatus(ProcessStatusEnum.CANCEL);
        internshipProcess.getLogDates().setUpdateDate(now);

        // Save the process with Assignee list
        self.insertProcessAssigneesAndUpdateProcessStatus(assigneeList, internshipProcess);
    }

    public void internshipExtensionRequest(InternshipExtensionRequestDto internshipExtensionRequestDto, Integer userId) {
        Integer dayNumber = 7;
        Date now = new Date();
        InternshipProcess internshipProcess = checkIfRequestIsPossible(internshipExtensionRequestDto.getProcessId(), userId, dayNumber);

        checkIfIsGivenWorkDayTrueOrThrowException(internshipProcess, internshipExtensionRequestDto);

        // Find assignee list and set process status to EXTEND
        List<ProcessAssignee> assigneeList = prepareProcessAssigneeList(internshipProcess, now);
        internshipProcess.setProcessStatus(ProcessStatusEnum.EXTEND);
        internshipProcess.setRequestedEndDate(internshipExtensionRequestDto.getRequestDate());
        internshipProcess.getLogDates().setUpdateDate(now);

        // Save the process with Assignee list
        self.insertProcessAssigneesAndUpdateProcessStatus(assigneeList, internshipProcess);
    }


    public void startInternshipApprovalProcess(Integer processId, Integer studentId) {
        InternshipProcess internshipProcess = getInternshipProcessIfExistsOrThrowException(processId);
        checkIfStudentIdAndInternshipProcessMatchesOrThrowException(studentId, internshipProcess.getStudent().getId());
        ArrayList<ProcessStatusEnum> expectedStatuses = new ArrayList<>();
        expectedStatuses.add(ProcessStatusEnum.FORM);
        expectedStatuses.add(ProcessStatusEnum.REJECTED);
        checkIfProcessStatusesMatchesOrThrowException(expectedStatuses, internshipProcess.getProcessStatus());

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

    public void evaluateInternshipProcess(InternshipProcessEvaluateRequest internshipProcessEvaluateRequest) {
        Integer processId = internshipProcessEvaluateRequest.getProcessId();
        Integer academicianId = internshipProcessEvaluateRequest.getAcademicianId();
        Boolean edit = internshipProcessEvaluateRequest.getReportEditRequest();

        if ((!internshipProcessEvaluateRequest.getApprove() || (edit != null && edit)) &&
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
        if ((edit != null && edit)) {
            // Edit request for report
            if (internshipProcess.getProcessStatus() != ProcessStatusEnum.REPORT1 &&
                    internshipProcess.getProcessStatus() != ProcessStatusEnum.REPORT2) {
                logger.error("Report edit request is not possible for this process status. Process status: "
                        + internshipProcess.getProcessStatus());
                throw new CustomException(HttpStatus.BAD_REQUEST);
            }
            internshipProcess.setAssignerId(academicianId);
            assigneeList = prepareProcessAssigneeList(internshipProcess, now);
            internshipProcess.setProcessStatus(ProcessStatusEnum.POST);
            internshipProcess.getLogDates().setUpdateDate(now);
        } else {
            if (!internshipProcessEvaluateRequest.getApprove()) {
                // Rejection
                if(internshipProcess.getProcessStatus() == ProcessStatusEnum.REPORT1) {
                    logger.error("Research assistants can only request edits. intenshipProcess Id: "
                            + internshipProcess.getId());
                }

                assigneeList = new ArrayList<>();
                internshipProcess.setAssignerId(academicianId);
                internshipProcess.getLogDates().setUpdateDate(now);
                internshipProcess.setComment(internshipProcessEvaluateRequest.getComment());

                if (internshipProcess.getProcessStatus() == ProcessStatusEnum.CANCEL) {
                    nextStatus = ProcessStatusEnum.IN1;
                } else if (internshipProcess.getProcessStatus() == ProcessStatusEnum.EXTEND) {
                    internshipProcess.setRequestedEndDate(null);
                    nextStatus = ProcessStatusEnum.EXTEND;
                } else if(internshipProcess.getProcessStatus() == ProcessStatusEnum.REPORT2) {
                    internshipProcess.setProcessStatus(ProcessStatusEnum.FAIL);
                }
                else {
                    nextStatus = ProcessStatusEnum.REJECTED;
                }
            } else {
                // Approval
                assigneeList = prepareProcessAssigneeList(internshipProcess, now);
                internshipProcess.setAssignerId(academicianId);
                internshipProcess.getLogDates().setUpdateDate(now);
                if (internshipProcess.getProcessStatus() == ProcessStatusEnum.CANCEL) {
                    // If the process is cancelled, delete the process
                    deleteInternshipProcess(processId);
                } else if (internshipProcess.getProcessStatus() == ProcessStatusEnum.EXTEND) {
                    internshipProcess.setEndDate(internshipProcess.getRequestedEndDate());
                    internshipProcess.setRequestedEndDate(null);
                    nextStatus = ProcessStatusEnum.IN1;
                } else {
                    nextStatus = ProcessStatusEnum.findNextStatus(internshipProcess.getProcessStatus());
                }
            }
        }

        // If the process is not cancelled, update the process status
        if (!(internshipProcess.getProcessStatus() == ProcessStatusEnum.CANCEL && internshipProcessEvaluateRequest.getApprove())) {
            internshipProcess.setProcessStatus(nextStatus);
            internshipProcess.setEditable(isNextStatusEditable(nextStatus));
            self.insertProcessAssigneesAndUpdateProcessStatus(assigneeList, internshipProcess);
        }
    }

    private InternshipProcess getInternshipProcessIfExistsOrThrowException(Integer processId) {
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

        // Check if the process is approved
        checkIfProcessStatusesMatchesOrThrowException(List.of(ProcessStatusEnum.IN1), internshipProcess.getProcessStatus());

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

    private void checkIfStudentIdAndInternshipProcessMatchesOrThrowException(Integer userId, Integer processId) {
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
            case PRE3 -> academicianService.findAcademicianIdsByAcademicAndDepartment(true, department.getId());
            /*case PRE4 -> academicianService.findAcademicianIdsByDeanAndDepartment(true, department.getId());*/
            case POST ->
                    academicianService.findAcademicianIdsByResearchAssistantAndDepartment(true, department.getId());
            case REPORT1 -> academicianService.findAcademicianIdsByAcademicAndDepartment(true, department.getId());
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
                                                             InternshipProcess internshipProcess) {
        processAssigneeService.deleteByProcessId(internshipProcess.getId());
        processAssigneeService.saveAll(processAssigneeList);
        internshipProcessDao.save(internshipProcess);
    }

    private boolean areFormFieldsEntered(InternshipProcess internshipProcess) {
        Set<String> excludedFields = new HashSet<>();
        excludedFields.add("assignerId");
        excludedFields.add("processAssignees");
        excludedFields.add("requestedEndDate");

        // TODO: BU ALANLAR KONTROL EDILECEK. SIMDILIK UPDATE'TE OLMADIGI ICIN SELIM'IN TESTLERI ICIN BOYLE YAPILDI.
        excludedFields.add("mufredatDurumuPath");
        excludedFields.add("transkriptPath");
        excludedFields.add("dersProgramıPath");
        excludedFields.add("stajRaporuPath");
        excludedFields.add("comment");

        Field[] fields = InternshipProcess.class.getDeclaredFields();

        for (Field field : fields) {
            String fieldName = field.getName();
            if (!excludedFields.contains(fieldName)) {
                field.setAccessible(true);
                try {
                    if (field.get(internshipProcess) == null) {
                        return false;  // If any non-excluded field is null, return false
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();  // Handle exception according to your needs
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isNextStatusEditable(ProcessStatusEnum nextStatus) {
        return nextStatus == ProcessStatusEnum.FORM || nextStatus == ProcessStatusEnum.REJECTED;
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

    private void copyEntityToDto(InternshipProcess internshipProcess, InternshipProcessGetResponse internshipProcessGetResponse) {
        BeanUtils.copyProperties(internshipProcess, internshipProcessGetResponse);

        if (internshipProcess.getCompany() != null) {
            internshipProcessGetResponse.setCompanyId(internshipProcess.getCompany().getId());
        }

        if (internshipProcess.getDepartment() != null) {
            internshipProcessGetResponse.setDepartmentId(internshipProcess.getDepartment().getId());
        }
    }
}
