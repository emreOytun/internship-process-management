package com.teamaloha.internshipprocessmanagement.entity;

import com.teamaloha.internshipprocessmanagement.entity.embeddable.LogDates;
import com.teamaloha.internshipprocessmanagement.enums.ProcessStatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "done_internship_process")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoneInternshipProcess {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Integer id;

    @Embedded
    LogDates logDates;

    @ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.DETACH}, fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "student_id")
    private Student student;

    @Column(name = "tc", nullable = true)
    private String tc;

    @Column(name = "student_number", nullable = true)
    private String studentNumber;

    @Column(name = "telephone_number", nullable = true)
    private String telephoneNumber;

    @Column(name = "class", nullable = true)
    private Integer classNumber;

    @Column(name = "internship_type", nullable = true)
    private String internshipType;

    @Column(name = "internship_number", nullable = true)
    private Integer internshipNumber;

    @Column(name = "start_date", nullable = true)
    private Date startDate;

    @Column(name = "end_date", nullable = true)
    private Date endDate;

    @ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.DETACH}, fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.DETACH}, fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(name = "engineer_mail", nullable = true)
    private String engineerMail;

    @Column(name = "engineer_name", nullable = true)
    private String engineerName;

    @Column(name = "donem_ici", nullable = true)
    private Boolean donem_ici;

    @Column(name = "mufredat_durumu_path", nullable = true)
    private String mufredatDurumuPath;

    @Column(name = "staj_raporu_path", nullable = true)
    private String stajRaporuPath;

}
