package com.teamaloha.internshipprocessmanagement.enums;

public enum ProcessStatusEnum {
    FORM,
    PRE1,
    PRE2,
    PRE3,
    PRE4,
    IN1,
    POST,
    REPORT1,
    REPORT2,
    DONE,
    FAIL,
    REJECTED,
    EXTEND,
    CANCEL;

    public static ProcessStatusEnum findNextStatus(ProcessStatusEnum processStatusEnum) {
        ProcessStatusEnum[] values = ProcessStatusEnum.values();
        int currentIndex = processStatusEnum.ordinal();
        int nextIndex = currentIndex + 1;
        return values[nextIndex];
    }
}
