package com.teamaloha.internshipprocessmanagement.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidEmailException extends CustomException {
    public InvalidEmailException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
