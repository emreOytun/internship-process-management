package com.teamaloha.internshipprocessmanagement.service;

import com.teamaloha.internshipprocessmanagement.dao.StudentDao;
import com.teamaloha.internshipprocessmanagement.dto.authentication.AuthenticationRequest;
import com.teamaloha.internshipprocessmanagement.dto.authentication.AuthenticationResponse;
import com.teamaloha.internshipprocessmanagement.dto.authentication.StudentRegisterRequest;
import com.teamaloha.internshipprocessmanagement.dto.user.UserDto;
import com.teamaloha.internshipprocessmanagement.entity.Student;
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

@Service
public class StudentService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final StudentDao studentDao;
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @Autowired
    public StudentService(StudentDao studentDao, UserService userService, AuthenticationService authenticationService) {
        this.studentDao = studentDao;
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    public AuthenticationResponse register(StudentRegisterRequest studentRegisterRequest) {
        boolean isMailExistsBefore = userService.existsByMail(studentRegisterRequest.getMail());
        if (isMailExistsBefore) {
            logger.error("Given mail exists before. Mail: " + studentRegisterRequest.getMail());
            throw new CustomException(ErrorCodeEnum.MAIL_EXISTS_BEFORE.getErrorCode(), HttpStatus.BAD_REQUEST);
        }

        // Convert given request dto to Academician entity.
        Student student = convertDtoToEntity(studentRegisterRequest);
        studentDao.save(student);

        // Create token and return it.
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(student, userDto);
        String jwtToken = authenticationService.createJwtToken(userDto);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    private Student convertDtoToEntity(StudentRegisterRequest studentRegisterRequest) {
        Student student = new Student();
        Date now = new Date();
        BeanUtils.copyProperties(studentRegisterRequest, student);
        student.setRoleEnum(RoleEnum.STUDENT);
        student.setPassword(authenticationService.hashPassword(studentRegisterRequest.getPassword()));
        student.setLogDates(LogDates.builder().createDate(now).updateDate(now).build());

        return student;
    }

    public AuthenticationResponse login(AuthenticationRequest authenticationRequest) {
        Student student = studentDao.findByMail(authenticationRequest.getMail());
        if (student == null) {
            logger.error("Invalid mail. mail: " + authenticationRequest.getMail());
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }

        if (!authenticationService.matchesPassword(authenticationRequest.getPassword(), student.getPassword())) {
            logger.error("Invalid password.");
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(student, userDto);
        String jwtToken = authenticationService.createJwtToken(userDto);
        AuthenticationResponse authenticationResponse = AuthenticationResponse.builder().token(jwtToken).build();
        authenticationResponse.setId(student.getId());
        return authenticationResponse;
    }

    public Student findStudentById(Integer id) {
        return studentDao.findStudentById(id);
    }
    public Student findByMail(String mail) {
        return studentDao.findByMail(mail);
    }
}
