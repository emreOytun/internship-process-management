package com.teamaloha.internshipprocessmanagement.entity;

import com.teamaloha.internshipprocessmanagement.entity.embeddable.LogDates;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "company")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Integer id;

    @Embedded
    LogDates logDates;

    @Column(name = "company_name", nullable = true)
    private String companyName;

    @Column(name = "company_mail", nullable = true)
    private String companyMail;

    @Column(name = "company_telephone", nullable = true)
    private String companyTelephone;

    @Column(name = "fax_number", nullable = true)
    private String faxNumber;

    @Column(name = "company_address", nullable = true)
    private String companyAddress;

    @Column(name = "start_date", nullable = true)
    private String startDate;

    @Column(name = "end_date", nullable = true)
    private String endDate;
}
