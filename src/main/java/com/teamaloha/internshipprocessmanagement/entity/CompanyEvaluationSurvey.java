package com.teamaloha.internshipprocessmanagement.entity;

import com.teamaloha.internshipprocessmanagement.entity.embeddable.LogDates;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "company_evaluation_survey")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CompanyEvaluationSurvey {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Integer id;

    @Embedded
    LogDates logDates;

    @OneToOne(cascade = {CascadeType.REFRESH, CascadeType.DETACH}, fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "internship_process_id")
    private InternshipProcess internshipProcess;

    @Column(name = "find_ownselves", nullable = false)
    private Boolean findOwnselves;

    @Column(name = "engineer_helpful", nullable = false)
    private Boolean engineerHelpful;

    @Column(name = "expectations_met", nullable = false)
    private Boolean expectationsMet;

    @Column(name = "salary", nullable = false)
    private Boolean salary;

    @Column(name = "insurance", nullable = false)
    private Boolean insurance;

    @Column(name = "food", nullable = false)
    private Boolean food;

    @Column(name = "transportation", nullable = false)
    private Boolean transportation;

    @Column(name = "shelter", nullable = false)
    private Boolean shelter;

    @Column(name = "recommendation", nullable = false)
    private Boolean recommendation;

    @Column(name = "problem", nullable = false)
    private Boolean problem;

    @Column(name = "problem_description", nullable = false)
    private String problemDescription;

    @Column(name = "network", nullable = false)
    private Boolean network;

    @Column(name = "network_description", nullable = false)
    private String networkDescription;

    @Column(name = "professional_pov", nullable = false)
    private Integer professionalPov;

    @Column(name = "social_pov", nullable = false)
    private Integer socialPov;

    @Column(name = "shared_with_company", nullable = false)
    private Boolean sharedWithCompany;
}
