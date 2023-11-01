package com.teamaloha.internshipprocessmanagement.enums;

public enum ErrorCodeEnum {
    MAIL_EXISTS_BEFORE("10");

    private String errorCode;

    private ErrorCodeEnum(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
