package com.teamaloha.internshipprocessmanagement.entity;

import com.teamaloha.internshipprocessmanagement.entity.embeddable.LogDates;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "company_staff")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyStaff {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Integer id;

    @Embedded
    LogDates logDates;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "surname", nullable = false)
    private String surname;

    @Column(name = "mail", nullable = false)
    private String mail;

    @Column(name = "telephone", nullable = true)
    private String telephone;

    @Column(name = "title", nullable = true)
    private String title;

    @Column(name = "department", nullable = true)
    private String department;

    @ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.DETACH}, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id")
    private Company company;
}
