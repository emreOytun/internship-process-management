package com.teamaloha.internshipprocessmanagement.handlers;

import com.teamaloha.internshipprocessmanagement.exceptions.CustomException;
import com.teamaloha.internshipprocessmanagement.results.ErrorResult;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResult> handleCustomerBusinessRulesException(CustomException exception) {
        ErrorResult errorResult = new ErrorResult(exception.getMessage());
        return new ResponseEntity<>(errorResult, exception.getHttpStatus());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResult> handleEntityNotFoundException(EntityNotFoundException exception) {
        ErrorResult errorResult = new ErrorResult(exception.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.NOT_FOUND);
    }
}
