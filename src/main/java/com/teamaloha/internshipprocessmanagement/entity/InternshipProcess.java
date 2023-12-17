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
@Table(name = "internship_process")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InternshipProcess {
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

    @Column(name = "position", nullable = true)
    private String position;

    @Column(name = "internship_type", nullable = true)
    private String internshipType;

    @Column(name = "internship_number", nullable = true)
    private Integer internshipNumber;

    @Column(name = "start_date", nullable = true)
    private Date startDate;

    @Column(name = "end_date", nullable = true)
    private Date endDate;

    @Column(name = "requested_end_date", nullable = true)
    private Date requestedEndDate;

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

    @Column(name = "choice_reason", nullable = true)
    private String choiceReason;

    @Column(name = "sgk_entry", nullable = true)
    private Boolean sgkEntry;

    @Column(name = "gss_entry", nullable = true)
    private Boolean gssEntry;

    @Column(name = "assigner_id", nullable = true)
    private Integer assignerId;

    @Column(name = "mustehaklik_belgesi_path", nullable = true)
    private String mustehaklikBelgesiPath;

    @Column(name = "staj_yeri_formu_path", nullable = true)
    private String stajYeriFormuPath;

    @Column(name = "donem_ici", nullable = true)
    private Boolean donem_ici;

    @Column(name = "mufredat_durumu_path", nullable = true)
    private String mufredatDurumuPath;

    @Column(name = "transkript_path", nullable = true)
    private String transkriptPath;

    @Column(name = "ders_programı_path", nullable = true)
    private String dersProgramıPath;

    @Column(name = "staj_raporu_path", nullable = true)
    private String stajRaporuPath;

    @Column(name = "process_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ProcessStatusEnum processStatus;

    // TODO: Nullable = false yap
    @Column(name = "editable")
    private Boolean editable;

    @Column(name = "comment", nullable = true)
    private String comment;

    @OneToMany(mappedBy = "internshipProcess", cascade = {CascadeType.DETACH, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    private Set<ProcessAssignee> processAssignees;
}
