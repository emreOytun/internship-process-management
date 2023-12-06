package com.teamaloha.internshipprocessmanagement.service;

import com.teamaloha.internshipprocessmanagement.dto.mail.SendMailRequest;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class MailService {
    private final Environment environment;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    private final JavaMailSender emailSender;

    @Autowired
    public MailService(Environment environment, JavaMailSender emailSender) {
        this.environment = environment;
        this.emailSender = emailSender;
    }

    public void sendMail(SendMailRequest sendMailRequest) {
        List<String> to = sendMailRequest.getTo();
        List<String> cc = sendMailRequest.getCc();
        String subject = sendMailRequest.getSubject();
        String text = sendMailRequest.getText();

        sendMail(to, cc, subject, text);
    }

    public void sendMail(List<String> to, List<String> cc, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to.toArray(new String[0]));

        if (cc != null && !cc.isEmpty()) {
            message.setCc(cc.toArray(new String[0]));
        }

        message.setSubject(subject);
        message.setText(text);

        String fromAddress = environment.getProperty("spring.mail.username");
        message.setFrom(fromAddress);


        emailSender.send(message);

        logger.info("Mail sent to: " + to);
    }

}
