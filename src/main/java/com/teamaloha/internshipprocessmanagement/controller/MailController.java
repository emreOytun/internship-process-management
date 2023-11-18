package com.teamaloha.internshipprocessmanagement.controller;

import com.teamaloha.internshipprocessmanagement.dto.mail.SendMailRequest;
import com.teamaloha.internshipprocessmanagement.service.MailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mail")
public class MailController {

    private final MailService mailService;
    @Autowired
    public MailController(MailService mailService) {
        this.mailService = mailService;
    }

    @GetMapping("/send-mail")
    @ResponseStatus(HttpStatus.OK)
    public void sendMail(@RequestBody @Valid SendMailRequest sendMailRequest) {
        mailService.sendMail(sendMailRequest);
    }

}
