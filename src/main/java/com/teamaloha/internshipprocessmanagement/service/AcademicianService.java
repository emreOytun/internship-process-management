package com.teamaloha.internshipprocessmanagement.service;

import com.teamaloha.internshipprocessmanagement.dao.AcademicianDao;
import com.teamaloha.internshipprocessmanagement.dto.InternshipProcess.InternshipProcessGetResponse;
import com.teamaloha.internshipprocessmanagement.dto.academician.AcademicsGetAllResponse;
import com.teamaloha.internshipprocessmanagement.dto.academician.AcademicsGetStudentAllProcessResponse;
import com.teamaloha.internshipprocessmanagement.dto.authentication.AcademicianRegisterRequest;
import com.teamaloha.internshipprocessmanagement.dto.authentication.AuthenticationRequest;
import com.teamaloha.internshipprocessmanagement.dto.authentication.AuthenticationResponse;
import com.teamaloha.internshipprocessmanagement.dto.user.UserDto;
import com.teamaloha.internshipprocessmanagement.entity.Academician;
import com.teamaloha.internshipprocessmanagement.entity.Department;
import com.teamaloha.internshipprocessmanagement.entity.InternshipProcess;
import com.teamaloha.internshipprocessmanagement.entity.embeddable.LogDates;
import com.teamaloha.internshipprocessmanagement.enums.ErrorCodeEnum;
import com.teamaloha.internshipprocessmanagement.enums.RoleEnum;
import com.teamaloha.internshipprocessmanagement.exceptions.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service
public class AcademicianService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final AcademicianDao academicianDao;
    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final DepartmentService departmentService;
    private final InternshipProcessService internshipProcessService;

    @Autowired
    public AcademicianService(AcademicianDao academicianDao, UserService userService,
                              DepartmentService departmentService, AuthenticationService authenticationService, InternshipProcessService internshipProcessService) {
        this.academicianDao = academicianDao;
        this.userService = userService;
        this.departmentService = departmentService;
        this.authenticationService = authenticationService;
        this.internshipProcessService = internshipProcessService;
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
        academician.setAcademic(false);
        academician.setValidated(false);
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
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    public List<Integer> findAcademicianIdsByInternshipCommitteeAndDepartment(Boolean internshipCommittee, Integer departmentId) {
        return academicianDao.findAcademicianIdsByInternshipCommitteeAndDepartment(internshipCommittee, departmentId);
    }

    public List<Integer> findAcademicianIdsByDepartmentChairAndDepartment(Boolean departmentChair, Integer departmentId) {
        return academicianDao.findAcademicianIdsByDepartmentChairAndDepartment(departmentChair, departmentId);
    }

    public List<Integer> findAcademicianIdsByExecutiveAndDepartment(Boolean executive, Integer departmentId) {
        return academicianDao.findAcademicianIdsByExecutiveAndDepartment(executive, departmentId);
    }

    public List<Integer> findAcademicianIdsByAcademicAndDepartment(Boolean academic, Integer departmentId) {
        return academicianDao.findAcademicianIdsByAcademicAndDepartment(academic, departmentId);
    }

    public AcademicsGetAllResponse getAllAcademics(Integer adminId) {
        // TODO : Add if user is admin if not throw expection

        List<Academician> academicianList = academicianDao.findAll();

        return new AcademicsGetAllResponse(academicianList);
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

    public AcademicsGetStudentAllProcessResponse getStudentAllProcess(Integer studentId, Integer academicianId) {
        getAcademicianIfExistsOrThrowException(academicianId);
        List<InternshipProcessGetResponse> processList = internshipProcessService.getAllInternshipProcess(studentId).getInternshipProcessList();


        return new AcademicsGetStudentAllProcessResponse(processList);
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
