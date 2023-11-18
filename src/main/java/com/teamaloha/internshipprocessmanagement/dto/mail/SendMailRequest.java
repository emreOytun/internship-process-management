package com.teamaloha.internshipprocessmanagement.dto.mail;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendMailRequest {
    @NotBlank
    String to;

    @NotBlank
    String cc;

    @NotBlank
    String subject;

    @NotBlank
    String text;

}
