package com.teamaloha.internshipprocessmanagement.service;

import ch.qos.logback.classic.Logger;
import com.teamaloha.internshipprocessmanagement.exceptions.CustomException;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.security.SecureRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtilityService {

    public static String generateRandomString() {
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder randomString = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < 10; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            randomString.append(characters.charAt(randomIndex));
        }
        return randomString.toString();
    };

    public static Boolean checkMailIsValid(String email) {
        Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@gtu.edu.tr");
        Matcher mat = pattern.matcher(email);

        return mat.matches();
    }
}

