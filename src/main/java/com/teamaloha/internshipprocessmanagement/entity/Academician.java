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
    @OneToOne(cascade = {CascadeType.REFRESH, CascadeType.DETACH}, fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(name = "approval_authorithy")
    private Boolean approvalAuthority;

    @Column(name = "validated")
    private Boolean validated;
}
