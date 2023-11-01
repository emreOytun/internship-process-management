package com.teamaloha.internshipprocessmanagement.enums;

public enum RoleEnum {
    ACADEMICIAN("ACADEMICIAN"),
    STUDENT("STUDENT");

    private RoleEnum(String value) {
        this.value = value;
    }

    private String value;

    public String getValue() {
        return value;
    }

}
