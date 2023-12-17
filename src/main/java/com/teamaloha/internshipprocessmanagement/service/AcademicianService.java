package com.teamaloha.internshipprocessmanagement.service;

import com.teamaloha.internshipprocessmanagement.dao.AcademicianDao;
import com.teamaloha.internshipprocessmanagement.dto.SearchByPageDto;
import com.teamaloha.internshipprocessmanagement.dto.SearchCriteria;
import com.teamaloha.internshipprocessmanagement.dto.SearchDto;
import com.teamaloha.internshipprocessmanagement.dto.academician.AcademicianGetResponse;
import com.teamaloha.internshipprocessmanagement.dto.academician.AcademicianSearchDto;
import com.teamaloha.internshipprocessmanagement.dto.academician.AcademicsGetAllResponse;
import com.teamaloha.internshipprocessmanagement.dto.authentication.AcademicianRegisterRequest;
import com.teamaloha.internshipprocessmanagement.dto.authentication.AuthenticationRequest;
import com.teamaloha.internshipprocessmanagement.dto.authentication.AuthenticationResponse;
import com.teamaloha.internshipprocessmanagement.dto.user.UserDto;
import com.teamaloha.internshipprocessmanagement.entity.Academician;
import com.teamaloha.internshipprocessmanagement.entity.Department;
import com.teamaloha.internshipprocessmanagement.entity.embeddable.LogDates;
import com.teamaloha.internshipprocessmanagement.enums.ErrorCodeEnum;
import com.teamaloha.internshipprocessmanagement.enums.RoleEnum;
import com.teamaloha.internshipprocessmanagement.exceptions.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class AcademicianService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final AcademicianDao academicianDao;
    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final DepartmentService departmentService;
    private final FiltersSpecification<Academician> filtersSpecification;

    @Autowired
    public AcademicianService(AcademicianDao academicianDao, UserService userService,
                              DepartmentService departmentService, AuthenticationService authenticationService,
                              FiltersSpecification filtersSpecification) {
        this.academicianDao = academicianDao;
        this.userService = userService;
        this.departmentService = departmentService;
        this.authenticationService = authenticationService;
        this.filtersSpecification = filtersSpecification;
    }

    public AuthenticationResponse register(AcademicianRegisterRequest academicianRegisterRequest) {
        boolean isMailExistsBefore = userService.existsByMail(academicianRegisterRequest.getMail());
        if (isMailExistsBefore) {
            logger.error("Given mail exists before. Mail: " + academicianRegisterRequest.getMail());
            throw new CustomException(ErrorCodeEnum.MAIL_EXISTS_BEFORE.getErrorCode(), HttpStatus.BAD_REQUEST);
        }

        Department department = departmentService.findDepartmentById(academicianRegisterRequest.getDepartmentId());
        if (department == null) {
            logger.error("Department with given id cannot be found. Department id: "
                    + academicianRegisterRequest.getDepartmentId());
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }

        // Convert given request dto to Academician entity.
        Academician academician = convertDtoToEntity(academicianRegisterRequest, department);
        academicianDao.save(academician);

        // Create token and return it.
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(academician, userDto);
        String jwtToken = authenticationService.createJwtToken(userDto);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    private Academician convertDtoToEntity(AcademicianRegisterRequest academicianRegisterRequest, Department department) {
        Academician academician = new Academician();
        Date now = new Date();
        BeanUtils.copyProperties(academicianRegisterRequest, academician);
        academician.setDepartment(department);
        academician.setRoleEnum(RoleEnum.ACADEMICIAN);
        academician.setPassword(authenticationService.hashPassword(academicianRegisterRequest.getPassword()));
        academician.setInternshipCommittee(false);
        academician.setDepartmentChair(false);
        academician.setExecutive(false);
        academician.setOfficer(false);
        academician.setValidated(false);
        academician.setIs_admin(false);
        academician.setLogDates(LogDates.builder().createDate(now).updateDate(now).build());
        return academician;
    }

    public AuthenticationResponse login(AuthenticationRequest authenticationRequest) {
        Academician academician = academicianDao.findByMail(authenticationRequest.getMail());
        if (academician == null) {
            logger.error("Invalid mail. mail: " + authenticationRequest.getMail());
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }

        if (!authenticationService.matchesPassword(authenticationRequest.getPassword(), academician.getPassword())) {
            logger.error("Invalid password.");
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(academician, userDto);
        String jwtToken = authenticationService.createJwtToken(userDto);
        AuthenticationResponse authenticationResponse = AuthenticationResponse.builder().token(jwtToken).build();
        authenticationResponse.setId(academician.getId());
        return authenticationResponse;
    }

    // TODO : abi burada admin id kontrolü felan lazım ama yapmadım
    // task id 1- internshipCommittee 2- departmentChair  3-  executive 4- academic
    public boolean assignTask(Integer academicianId, Integer taskId){

        // TODO : Add assign task
        Academician academician = getAcademicianIfExistsOrThrowException(academicianId);
        switch(taskId){
            case 1:
                academician.setInternshipCommittee(true);
                break;
            case 2:
                academician.setDepartmentChair(true);
                break;
            case 3:
                academician.setExecutive(true);
                break;
            case 4:
                academician.setAcademic(true);
                break;
            default:
                logger.error("Invalid task id. Task id: " + taskId);
                throw new CustomException(HttpStatus.BAD_REQUEST);
        }
        academicianDao.save(academician);
        logger.info("Task assigned. Academician id: " + academicianId + " Task id: " + taskId);
        return true;
    }

    public List<Integer> findAcademiciansIdsByInternshipCommitteeAndDepartment(Boolean internshipCommittee, Integer departmentId) {
        return academicianDao.findAcademiciansIdsByInternshipCommitteeAndDepartment(internshipCommittee, departmentId);
    }

    public List<Integer> findAcademicianIdsByDepartmentChairAndDepartment(Boolean departmentChair, Integer departmentId) {
        return academicianDao.findAcademicianIdsByDepartmentChairAndDepartment(departmentChair, departmentId);
    }

    public List<Integer> findAcademicianIdsByExecutiveAndDepartment(Boolean executive, Integer departmentId) {
        return academicianDao.findAcademicianIdsByExecutiveAndDepartment(executive, departmentId);
    }


    public List<Integer> findAcademicianIdsByOfficerAndDepartment(Boolean officer, Integer departmentId) {
        return academicianDao.findAcademicianIdsByOfficerAndDepartment(officer, departmentId);
    }

    public List<Integer> findAcademicianIdsByDeanAndDepartment(Boolean dean, Integer departmentId) {
        return academicianDao.findAcademicianIdsByDeanAndDepartment(dean, departmentId);
    }

    public List<Integer> findAcademicianIdsByResearchAssistantAndDepartment(Boolean researchAssistant, Integer departmentId) {
        return academicianDao.findAcademicianIdsByResearchAssistantAndDepartment(researchAssistant, departmentId);
    }

    public List<Integer> findAcademicianIdsByAcademicAndDepartment(Boolean academic, Integer departmentId) {
        return academicianDao.findAcademicianIdsByAcademicAndDepartment(academic, departmentId);
    }

    public AcademicsGetAllResponse getAllAcademics(AcademicianSearchDto academicianSearchDto) {
        List<Academician> academicianList = academicianDao.findAll(prepareAcademicianSpecification(academicianSearchDto),
                SearchByPageDto.getPageable(academicianSearchDto.getSearchByPageDto())).toList();
        return createAcademicianGetAllResponse(academicianList);
    }
    public AcademicsGetAllResponse getAllAcademics() {
        List<Academician> academicianList = academicianDao.findAllAcademicians();
        return createAcademicianGetAllResponse(academicianList);
    }
    private AcademicsGetAllResponse createAcademicianGetAllResponse(List<Academician> academicianList) {
        List<AcademicianGetResponse> academicianGetResponseList = new ArrayList<>();
        for (Academician academician : academicianList) {
            academicianGetResponseList.add(convertEntityToDto(academician));
        }
        return AcademicsGetAllResponse.builder().academicsList(academicianList).build();
    }

    private AcademicianGetResponse convertEntityToDto(Academician academician) {
        AcademicianGetResponse academicianGetResponse = new AcademicianGetResponse();
        BeanUtils.copyProperties(academician, academicianGetResponse);
        if (academician.getDepartment() != null) {
            academicianGetResponse.setDepartmentName(academician.getDepartment().getDepartmentName());
        }
        return academicianGetResponse;
    }

    private Specification<Academician> prepareAcademicianSpecification(AcademicianSearchDto academicianSearchDto) {
        Map<String, Comparable[]> criteriaMap = new HashMap<>();

        if (academicianSearchDto.getName() != null) {
            criteriaMap.put("firstName", new Comparable[]{academicianSearchDto.getName(), SearchCriteria.Operation.LIKE});
            criteriaMap.put("lastName", new Comparable[]{academicianSearchDto.getName(), SearchCriteria.Operation.LIKE});
        }

        if (academicianSearchDto.getCreateDateStart() != null) {
            criteriaMap.put("createDate", new Comparable[]{academicianSearchDto.getCreateDateStart(), SearchCriteria.Operation.GREATER_THAN});
        }

        if (academicianSearchDto.getCreateDateEnd() != null) {
            criteriaMap.put("createDate", new Comparable[]{academicianSearchDto.getCreateDateEnd(), SearchCriteria.Operation.LESS_THAN});
        }

        return filtersSpecification.getSearchSpecification(filtersSpecification.convertMapToSearchCriteriaList(criteriaMap),
                                                            SearchDto.LogicOperator.AND);
    }

    public void validateAcademician(Integer academecianId, Integer adminId) {
        // TODO : Add if user is admin if not throw expection

        Academician academician = getAcademicianIfExistsOrThrowException(academecianId);
        academician.setValidated(true);
        academicianDao.save(academician);

        logger.info("Academician validated with ID: " + academecianId);
    }

    public void assignDepartmentToAcademician(Integer academicianId, Integer departmentId, Integer adminId) {
        // TODO : Add if user is admin if not throw expection

        Academician academician = getAcademicianIfExistsOrThrowException(academicianId);
        Department department = departmentService.getDepartmentIfExistsOrThrowException(departmentId);
        academician.setDepartment(department);
        academicianDao.save(academician);

        logger.info("Academician's department updated. Academician ID: " + academicianId);
    }

    public Academician getAcademicianIfExistsOrThrowException(Integer academicianId) {
        Academician academician = academicianDao.findAcademicianById(academicianId);
        if (academician == null) {
            logger.error("The academician id given does not exist. Academician id: "
                    + academicianId);
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
        return academician;
    }

}
