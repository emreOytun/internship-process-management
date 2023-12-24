package com.teamaloha.internshipprocessmanagement.entity;

import com.teamaloha.internshipprocessmanagement.entity.embeddable.LogDates;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "faculty")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Faculty {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Integer id;

    @Embedded
    LogDates logDates;

    @Column(name = "faculty_name", nullable = false)
    private String facultyName;
}
