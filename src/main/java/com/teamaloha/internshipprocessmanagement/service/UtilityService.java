package com.teamaloha.internshipprocessmanagement.service;

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
    }

    public static Boolean checkMailIsValid(String email) {
        Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@gtu.edu.tr");
        Matcher mat = pattern.matcher(email);

        return mat.matches();
    }

    public static boolean checkPasswordValid(String password) {
        // Check password length
        if (password.length() < 8 && password.length() > 1000) {
            return false;
        }

        boolean uppercaseFound = false;
        boolean lowercaseFound = false;
        boolean digitFound = false;
        boolean specialCharacterFound = false;

        // Check characters in the password
        for (char character : password.toCharArray()) {
            if (Character.isUpperCase(character)) {
                uppercaseFound = true;
            } else if (Character.isLowerCase(character)) {
                lowercaseFound = true;
            } else if (Character.isDigit(character)) {
                digitFound = true;
            } else if (!Character.isLetterOrDigit(character)) {
                specialCharacterFound = true;
            }

            // Break the loop if all required features are found
            if (uppercaseFound && lowercaseFound && digitFound && specialCharacterFound) {
                break;
            }
        }

        // Check if all required features are present
        return uppercaseFound && lowercaseFound && digitFound && specialCharacterFound;
    }

    public static void main(String[] args) {
//        String mail = "academician3@gmail.com";
//        System.out.println(checkMailIsValid(mail));

        String password = "NANE1234.";
        System.out.println(checkPasswordValid(password));
    }
}

