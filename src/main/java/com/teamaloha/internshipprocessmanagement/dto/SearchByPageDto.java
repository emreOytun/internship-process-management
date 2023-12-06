package com.teamaloha.internshipprocessmanagement.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Objects;

@Data
@NoArgsConstructor
public class SearchByPageDto {
    private Integer pageNo;
    private Integer pageSize;
    private Sort.Direction sort;
    private String sortByColumn;

    public static Pageable getPageable(SearchByPageDto dto) {
        Integer page = Objects.nonNull(dto.getPageNo()) ? dto.getPageNo() : 0;
        Integer size = Objects.nonNull(dto.getPageSize()) ? dto.getPageSize() : 10;
        Sort.Direction sort = Objects.nonNull(dto.getSort()) ? dto.getSort() : Sort.Direction.ASC;
        String sortByColumn = Objects.nonNull(dto.getSortByColumn()) ? dto.getSortByColumn() : "logDates.updateDate";
        return PageRequest.of(page, size, sort, sortByColumn);
    }
}
