package com.teamaloha.internshipprocessmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Objects;

@Data
@NoArgsConstructor
public class SearchByPageDto {
    private Integer pageNo = 0;
    private Integer pageSize = 0;
    private Sort.Direction sort = Sort.Direction.ASC;
    private String sortByColumn = "updateDate";

    public Pageable getPageable(SearchByPageDto dto) {
        Integer page = Objects.nonNull(dto.getPageNo()) ? dto.getPageNo() : this.getPageNo();
        Integer size = Objects.nonNull(dto.getPageSize()) ? dto.getPageSize() : this.getPageSize();
        Sort.Direction sort = Objects.nonNull(dto.getSort()) ? dto.getSort() : this.sort;
        String sortByColumn = Objects.nonNull(dto.getSortByColumn()) ? dto.getSortByColumn() : this.sortByColumn;
        return PageRequest.of(page, size, sort, sortByColumn);
    }
}
