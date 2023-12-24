package com.teamaloha.internshipprocessmanagement.entity;

import com.teamaloha.internshipprocessmanagement.entity.embeddable.LogDates;
import com.teamaloha.internshipprocessmanagement.enums.RoleEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "academician")
@AllArgsConstructor
@NoArgsConstructor
public class Academician extends User{
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
