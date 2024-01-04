package com.teamaloha.internshipprocessmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "academician")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
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

    public Academician(String firstName, String lastName) {
        setFirstName(firstName);
        setLastName(lastName);
    }

}
