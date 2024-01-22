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
import com.teamaloha.internshipprocessmanagement.service.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StudentService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final StudentDao studentDao;
    private final UserService userService;
    private final AuthenticationService authenticationService;

    private final JwtService jwtService;

    private final MailService mailService;

    @Autowired
    public StudentService(StudentDao studentDao, UserService userService, AuthenticationService authenticationService, MailService mailService, JwtService jwtService){
        this.studentDao = studentDao;
        this.userService = userService;
        this.authenticationService = authenticationService;
        this.mailService = mailService;
        this.jwtService = jwtService;
    }

    public AuthenticationResponse register(StudentRegisterRequest studentRegisterRequest) {
        //if (!UtilityService.checkMailIsValid(studentRegisterRequest.getMail())) {
        //    logger.error("Invalid mail. Mail: " + studentRegisterRequest.getMail());
        //    throw new CustomException(ErrorCodeEnum.MAIL_FORMAT_NOT_VALID.getErrorCode(), HttpStatus.BAD_REQUEST);
        //}
        if (!UtilityService.checkPasswordValid(studentRegisterRequest.getPassword())) {
            logger.error("Invalid password. Password: " + studentRegisterRequest.getPassword());
            throw new CustomException(ErrorCodeEnum.PASSWORD_FORMAT_NOT_VALID.getErrorCode(), HttpStatus.BAD_REQUEST);
        }

        boolean isMailExistsBefore = userService.existsByMail(studentRegisterRequest.getMail());
        if (isMailExistsBefore) {
            logger.error("Given mail exists before. Mail: " + studentRegisterRequest.getMail());
            throw new CustomException(ErrorCodeEnum.MAIL_EXISTS_BEFORE.getErrorCode(), HttpStatus.BAD_REQUEST);
        }

        // Convert given request dto to Academician entity.
        Student student = convertDtoToEntity(studentRegisterRequest);
        student.setVerifiedMail(false);
        // create 6 digit verification code
        Random random = new Random();
        int code = random.nextInt(999999);
        student.setVerificationCode(String.format("%06d", code));
        mailService.sendMail(
                Arrays.asList(student.getMail()),
                null,
                "Öğrenci Kaydı",
                "Öğrenci kaydınızı tamamlamak için aşağıdaki linke tıklayınız ve "+student.getVerificationCode()+" kodunu Giriniz"+": https://subtle-scone-3209de.netlify.app/onayla/auth");
        studentDao.save(student);

        // Create token and return it.
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(student, userDto);
        String jwtToken = authenticationService.createJwtToken(userDto);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .fullName(getFullName(student))
                .build();
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
            throw new CustomException(ErrorCodeEnum.MAIL_NOT_EXISTS_BEFORE.getErrorCode(), HttpStatus.BAD_REQUEST);
        }

        if (!authenticationService.matchesPassword(authenticationRequest.getPassword(), student.getPassword())) {
            logger.error("Invalid password.");
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }

        if(!student.getVerifiedMail()) {
            // create 6 digit verification code
            Random random = new Random();
            int code = random.nextInt(999999);
            student.setVerificationCode(String.format("%06d", code));
            mailService.sendMail(
                    Arrays.asList(student.getMail()),
                    null,
                    "Öğrenci Kaydı",
                    "Öğrenci kaydınızı tamamlamak için aşağıdaki linke tıklayınız ve "+student.getVerificationCode()+" kodunu Giriniz"+": https://subtle-scone-3209de.netlify.app/onayla/auth");
            studentDao.save(student);

            logger.error("Mail is not verified. UserId: " + student.getId());
            throw new CustomException(ErrorCodeEnum.MAIL_NOT_VERIFIED.getErrorCode(), HttpStatus.BAD_REQUEST);
        }

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(student, userDto);
        String jwtToken = authenticationService.createJwtToken(userDto);
        AuthenticationResponse authenticationResponse =
                AuthenticationResponse.builder()
                .fullName(getFullName(student))
                .token(jwtToken).build();
        authenticationResponse.setId(student.getId());
        return authenticationResponse;
    }
    public boolean verify(String code, String mail) {
        Student student = studentDao.findByMail(mail);
        if (student == null) {
            logger.error("Invalid mail. mail: " + mail);
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
        String verificationCode = student.getVerificationCode();
        if(!verificationCode.equals(code)){
            logger.error("Invalid code. code: " + code);
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
        student.setVerifiedMail(true);
        studentDao.save(student);
        return true;
    }
    public void forgotPassword(String email) {
        Student student = studentDao.findByMail(email);
        if (student == null) {
            logger.error("Invalid mail. mail: " + email);
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
        List<String> to = new ArrayList<>();
        to.add(student.getMail());

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(student, userDto);
        String token = UtilityService.generateRandomString();
        student.setPasswordResetToken(token);
        studentDao.save(student);

        this.mailService.sendMail(
                to,
                null,
                "Şifre Sıfırlama",
                "Şifrenizi sıfırlamak için aşağıdaki linke tıklayınız 50 dakika aktif olacaktır: https://subtle-scone-3209de.netlify.app/auth/resetPassword/"+token
        );
    }

    public void resetPassword(String token, String password) {
        Student student = studentDao.findByPasswordResetToken(token);
        if (student == null) {
            logger.error("Invalid token. token: " + token);
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }

        if(student.getPasswordResetToken().equals(token)){
            student.setPassword(authenticationService.hashPassword(password));
        }else{
            logger.error("Invalid token. token: " + token);
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }

        /*student.setPassword(authenticationService.hashPassword(password));*/
        student.setPasswordResetToken(null);
        studentDao.save(student);
    }

    public Student findStudentById(Integer id) {
        return studentDao.findStudentById(id);
    }
    public Student findByMail(String mail) {
        return studentDao.findByMail(mail);
    }

    private String getFullName(Student student) {
        return student.getFirstName() + " " + student.getLastName();
    }
}
