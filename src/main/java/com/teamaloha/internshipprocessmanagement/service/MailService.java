package com.teamaloha.internshipprocessmanagement.service;

import com.teamaloha.internshipprocessmanagement.dto.mail.SendMailRequest;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

@Service(value = "MailService")
public class MailService {
    private final Environment environment;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    private JavaMailSender emailSender;

    @Autowired
    public MailService(Environment environment) {
        this.environment = environment;
    }

    public void sendMail(SendMailRequest sendMailRequest) {
        String to = sendMailRequest.getTo();
        String cc = sendMailRequest.getCc();
        String subject = sendMailRequest.getSubject();
        String text = sendMailRequest.getText();

        sendMail(Collections.singletonList(to), cc, subject, text);
    }

    public void sendMail(List<String> to, String cc, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to.toArray(new String[0]));

        if (cc != null && !cc.isEmpty()) {
            message.setCc(cc);
        }

        message.setSubject(subject);
        message.setText(text);

        String fromAddress = environment.getProperty("mail.from.address", "noreply@example.com");
        message.setFrom(fromAddress);

        // Send the email
        emailSender.send(message);

        logger.info("Mail sent to: " + to);
    }

}
