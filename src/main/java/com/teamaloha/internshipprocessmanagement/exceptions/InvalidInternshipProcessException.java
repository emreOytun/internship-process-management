package com.teamaloha.internshipprocessmanagement.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidInternshipProcessException extends CustomException {
    public InvalidInternshipProcessException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
