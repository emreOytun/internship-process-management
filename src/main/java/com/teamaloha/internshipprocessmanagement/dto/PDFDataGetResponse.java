package com.teamaloha.internshipprocessmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PDFDataGetResponse {
    private byte[] pdfData;
    private String pdfName;
}
