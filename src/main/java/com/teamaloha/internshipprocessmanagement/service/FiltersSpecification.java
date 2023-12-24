package com.teamaloha.internshipprocessmanagement.service;

import com.teamaloha.internshipprocessmanagement.dto.SearchCriteria;
import com.teamaloha.internshipprocessmanagement.dto.SearchDto;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

    public List<SearchCriteria> convertMapToSearchCriteriaList(Map<String, Comparable[]> criteriaMap) {
        List<SearchCriteria> searchCriteriaList = new ArrayList<>();

        for (Map.Entry<String, Comparable[]> entry : criteriaMap.entrySet()) {
            String rootPath = entry.getKey();
            Comparable[] valuesAndOperation = entry.getValue();

            SearchCriteria searchCriteria = new SearchCriteria();
            searchCriteria.setRootPath(rootPath.split("\\."));
            searchCriteria.setValues(Arrays.copyOf(valuesAndOperation, valuesAndOperation.length - 1));
            searchCriteria.setOperation((SearchCriteria.Operation) valuesAndOperation[valuesAndOperation.length - 1]);

            searchCriteriaList.add(searchCriteria);
        }
        return searchCriteriaList;
    }
}
