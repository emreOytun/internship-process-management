package com.teamaloha.internshipprocessmanagement.dto.InternshipProcess;

import com.teamaloha.internshipprocessmanagement.enums.ProcessStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InternshipProcessGetResponse {

    private String fullName;

    private Date updateDate;

    private Integer id;

    private String tc;

    private String studentNumber;

    private Integer studentId;

    private String telephoneNumber;

    private Integer classNumber;

    private String position;

    private String internshipType;

    private Integer internshipNumber;

    private Date startDate;

    private Date endDate;

    private Integer companyId;

    private Integer departmentId;

    private String engineerMail;

    private String engineerName;

    private String choiceReason;

    private Boolean sgkEntry;

    private Boolean gssEntry;

    private Boolean editable;

    private Boolean donem_ici;

    private Integer stajRaporuID;

    private Integer mufredatDurumuID;

    private Integer transkriptID;

    private Integer dersProgramıID;

    private Integer stajYeriFormuID;

    private Integer mustehaklikBelgesiID;

    // @Enumerated(EnumType.STRING)
    private ProcessStatusEnum processStatus;

    private Boolean rejected;

    private ProcessStatusEnum rejectedStatus;

    private String comment;

    private String companyName;
}
