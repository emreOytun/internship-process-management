package com.teamaloha.internshipprocessmanagement.entity;

import com.teamaloha.internshipprocessmanagement.entity.embeddable.LogDates;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pdfdata")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PDFData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Embedded
    LogDates logDates;

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private String type;

    @Column(name = "fileOwner")
    private Integer fileOwnerId;

    @Lob
    @Column(name = "pdf_data", columnDefinition = "LONGBLOB")
    private byte[] data;
}
