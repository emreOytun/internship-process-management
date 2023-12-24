package com.teamaloha.internshipprocessmanagement.entity;

import com.teamaloha.internshipprocessmanagement.entity.embeddable.LogDates;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "department")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Integer id;

    @Embedded
    LogDates logDates;

    @Column(name = "department_name", nullable = false)
    private String departmentName;

    @ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.DETACH}, fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "faculty_id")
    private Faculty faculty;
}
