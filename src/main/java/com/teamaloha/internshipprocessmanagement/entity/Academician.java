package com.teamaloha.internshipprocessmanagement.entity;

import com.teamaloha.internshipprocessmanagement.entity.embeddable.LogDates;
import com.teamaloha.internshipprocessmanagement.enums.RoleEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "academician")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Academician {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Integer id;

    @Embedded
    LogDates logDates;

    @Column(name = "mail", nullable = false, unique = true)
    private String mail;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private RoleEnum roleEnum;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.DETACH}, fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(name = "internship_committee")
    private Boolean internshipCommittee;

    @Column(name = "department_chair")
    private Boolean departmentChair;

    @Column(name = "executive")
    private Boolean executive;

    @Column(name = "officer")
    private Boolean officer;

    @Column(name = "dean")
    private Boolean dean;

    @Column(name = "academic")
    private Boolean academic;

    @Column(name = "research_assistant")
    private Boolean researchAssistant;

    @Column(name = "validated")
    private Boolean validated;

    @Column(name = "is_admin")
    private Boolean is_admin;

}
