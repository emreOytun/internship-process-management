package com.teamaloha.internshipprocessmanagement.service;

import com.teamaloha.internshipprocessmanagement.dto.SearchCriteria;
import com.teamaloha.internshipprocessmanagement.dto.SearchDto;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Service
public class FiltersSpecification<T> {

    public Specification<T> getSearchSpecification(List<SearchCriteria> searchCriteriaList,
                                                   SearchDto.LogicOperator logicOperator) {
        return ((root, query, criteriaBuilder) -> {
           List<Predicate> predicateList = new ArrayList<>();

           for (SearchCriteria searchCriteria : searchCriteriaList) {
               Comparable[] values = searchCriteria.getValues();
               Path path = null;
               if (searchCriteria.getOperation() != SearchCriteria.Operation.JOIN) {
                   path = root.get(searchCriteria.getRootPath()[0]);
                   for (int i = 1; i < searchCriteria.getRootPath().length; ++i) {
                       path = path.get(searchCriteria.getRootPath()[i]);
                   }
               }

               Predicate predicate = switch (searchCriteria.getOperation()) {
                   case EQUAL -> criteriaBuilder.equal(path, values[0]);
                   case LIKE -> criteriaBuilder.like(path, values[0].toString());
                   case IN -> path.in(values);
                   case LESS_THAN -> criteriaBuilder.lessThan(path, values[0]);
                   case GREATER_THAN -> criteriaBuilder.greaterThan(path, values[0]);
                   case GREATER_THAN_OR_EQUAL_TO -> criteriaBuilder.greaterThanOrEqualTo(path, values[0]);
                   case LESS_THAN_OR_EQUAL_TO -> criteriaBuilder.lessThanOrEqualTo(path, values[0]);
                   case BETWEEN -> criteriaBuilder.between(path, values[0], values[1]);
                   case JOIN -> criteriaBuilder.equal(
                           root.join(searchCriteria.getJoinAttribute()).get(searchCriteria.getRootPath()[0]),
                           values[0]);
               };
               predicateList.add(predicate);
           }

           if (logicOperator == SearchDto.LogicOperator.AND) {
               return criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
           }
           return criteriaBuilder.or(predicateList.toArray(new Predicate[0]));
        });
    }
}
