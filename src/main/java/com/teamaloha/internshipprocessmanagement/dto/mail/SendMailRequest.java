package com.teamaloha.internshipprocessmanagement.dto.mail;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendMailRequest {
    @NotEmpty
    List<String> to;

    List<String> cc;

    @NotBlank
    String subject;

    @NotBlank
    String text;

}
