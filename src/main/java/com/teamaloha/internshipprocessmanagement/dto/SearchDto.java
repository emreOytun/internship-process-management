package com.teamaloha.internshipprocessmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchDto {
    private List<SearchCriteria> searchCriteria;
    private LogicOperator logicOperator;
    private SearchByPageDto searchByPageDto;

    public enum LogicOperator {
        AND, OR
    }
}
