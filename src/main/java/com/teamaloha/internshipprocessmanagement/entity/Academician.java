package com.teamaloha.internshipprocessmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "academician")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Academician extends User {
    @ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.DETACH}, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(name = "internship_committee")
    private Boolean internshipCommittee;

    @Column(name = "department_chair")
    private Boolean departmentChair;

    @Column(name = "executive")
    private Boolean executive;

    @Column(name = "academic")
    private Boolean academic;

    @Column(name = "validated")
    private Boolean validated;

    @Column(name = "is_admin")
    private Boolean is_admin;

}
