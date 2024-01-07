package com.teamaloha.internshipprocessmanagement.dto.InternshipProcess;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InternshipProcessUpdateRequest {
    @NotNull
    private Integer id;

    private String tc;

    private String studentNumber;

    private String telephoneNumber;

    private Integer classNumber;

    private String position;

    private String internshipType;

    private Integer internshipNumber;

    private Date startDate;

    private Date endDate;

    private Integer companyId;

    private Integer departmentId;

    @Email
    private String engineerMail;

    private String engineerName;

    private String choiceReason;

    private Boolean sgkEntry;

    private Boolean gssEntry;

    private Boolean donem_ici;

    private MultipartFile mustehaklikBelgesi;

    private MultipartFile stajYeriFormu;
}
