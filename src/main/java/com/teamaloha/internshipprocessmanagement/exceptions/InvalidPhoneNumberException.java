package com.teamaloha.internshipprocessmanagement.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidPhoneNumberException extends CustomException {
    public InvalidPhoneNumberException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
