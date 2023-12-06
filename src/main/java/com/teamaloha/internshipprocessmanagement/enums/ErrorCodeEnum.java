package com.teamaloha.internshipprocessmanagement.enums;

public enum ErrorCodeEnum {
    MAIL_EXISTS_BEFORE("10"),
    COMPANY_EXISTS_BEFORE("11"),
    COMPANY_NOT_EXISTS_BEFORE("12"),
    COMPANY_STAFF_NOT_EXISTS_BEFORE("13");

    private final String errorCode;

    ErrorCodeEnum(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
