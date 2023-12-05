package com.teamaloha.internshipprocessmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchCriteria {
    private String column;
    private String value;
    private String joinAttribute;
    private Operation operation;
    private DataType dataType;

    public enum Operation {
        EQUAL, LIKE, IN, GREATER_THAN, LESS_THAN, BETWEEN, JOIN;
    }

    public enum DataType {
        STRING, LONG, INTEGER, DATE;
    }
}
