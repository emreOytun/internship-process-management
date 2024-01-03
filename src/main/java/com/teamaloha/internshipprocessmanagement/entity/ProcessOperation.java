package com.teamaloha.internshipprocessmanagement.entity;

import com.teamaloha.internshipprocessmanagement.entity.embeddable.LogDates;
import com.teamaloha.internshipprocessmanagement.enums.ProcessOperationType;
import com.teamaloha.internshipprocessmanagement.enums.ProcessStatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "process_operation")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProcessOperation {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Integer id;

    @Embedded
    LogDates logDates;

    @Column(name = "process_id", nullable = false)
    private Integer processId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "old_status", nullable = true)
    @Enumerated(EnumType.STRING)
    private ProcessStatusEnum oldStatus;

    @Column(name = "new_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ProcessStatusEnum newStatus;

    @Column(name = "operation_type", nullable = true)
    @Enumerated(EnumType.STRING)
    private ProcessOperationType operationType;

    @Column(name = "comment", nullable = true)
    private String comment;
}
