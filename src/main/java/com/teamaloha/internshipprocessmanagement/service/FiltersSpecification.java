package com.teamaloha.internshipprocessmanagement.service;

import com.teamaloha.internshipprocessmanagement.dto.SearchCriteria;
import com.teamaloha.internshipprocessmanagement.dto.SearchDto;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class FiltersSpecification<T> {

    public Specification<T> getSearchSpecification(SearchCriteria searchCriteria) {
        return new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get(searchCriteria.getColumn()), searchCriteria.getValue());
            }
        };
    }

    public Specification<T> getSearchSpecification(List<SearchCriteria> searchCriteriaList,
                                                   SearchDto.LogicOperator logicOperator) throws ParseException {
        return ((root, query, criteriaBuilder) -> {
           List<Predicate> predicateList = new ArrayList<>();

           for (SearchCriteria searchCriteria : searchCriteriaList) {
               Predicate predicate = switch (searchCriteria.getOperation()) {
                   case EQUAL -> criteriaBuilder.equal(root.get(searchCriteria.getColumn()), searchCriteria.getValue());
                   case LIKE -> criteriaBuilder.like(root.get(searchCriteria.getColumn()), searchCriteria.getValue());
                   case IN -> {
                       String[] valueList = searchCriteria.getValue().split(",");
                       yield root.get(searchCriteria.getColumn()).in(valueList);
                   }
                   case LESS_THAN -> criteriaBuilder.lessThan(root.get(searchCriteria.getColumn()), searchCriteria.getValue());
                   case GREATER_THAN -> criteriaBuilder.greaterThan(root.get(searchCriteria.getColumn()), searchCriteria.getValue());
                   case BETWEEN -> {
                       String[] valueList = searchCriteria.getValue().split(",");
                       try {
                           if (searchCriteria.getDataType() == SearchCriteria.DataType.DATE) {
                               SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                               yield criteriaBuilder.between(root.get(searchCriteria.getColumn()),
                                       sdf.parse(valueList[0]),
                                       sdf.parse(valueList[1]));
                           }
                           else if (searchCriteria.getDataType() == SearchCriteria.DataType.INTEGER) {
                               yield criteriaBuilder.between(root.get(searchCriteria.getColumn()),
                                       Integer.valueOf(valueList[0]),
                                       Integer.valueOf(valueList[1]));
                           }
                           else if (searchCriteria.getDataType() == SearchCriteria.DataType.LONG) {
                               yield criteriaBuilder.between(root.get(searchCriteria.getColumn()),
                                       Long.valueOf(valueList[0]),
                                       Long.valueOf(valueList[1]));
                           }
                           else {
                               yield criteriaBuilder.between(root.get(searchCriteria.getColumn()),
                                       valueList[0],
                                       valueList[1]);
                           }
                       } catch (ParseException e) {
                           throw new RuntimeException(e);
                       }
                   }
                   case JOIN -> criteriaBuilder.equal(root.join(searchCriteria.getJoinAttribute()).get(searchCriteria.getColumn()),
                                                        searchCriteria.getValue());
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
