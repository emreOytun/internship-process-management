package com.teamaloha.internshipprocessmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchCriteria {
    private String[] rootPath;
    private Comparable[] values;
    private String joinAttribute;
    private Operation operation;
    private DataType dataType;

    public enum Operation {
        EQUAL, LIKE, IN, GREATER_THAN, LESS_THAN, GREATER_THAN_OR_EQUAL_TO, LESS_THAN_OR_EQUAL_TO, BETWEEN, JOIN
    }

    public enum DataType {
        STRING, LONG, INTEGER, DATE
    }
}
