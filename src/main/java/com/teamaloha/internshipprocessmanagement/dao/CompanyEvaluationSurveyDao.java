package com.teamaloha.internshipprocessmanagement.dao;

import com.teamaloha.internshipprocessmanagement.entity.CompanyEvaluationSurvey;
import com.teamaloha.internshipprocessmanagement.entity.InternshipProcess;
import org.springframework.data.jpa.repository.*;

import java.util.List;

public interface CompanyEvaluationSurveyDao extends JpaRepository<CompanyEvaluationSurvey, Integer>,
        JpaSpecificationExecutor<CompanyEvaluationSurvey> {

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {
                    "internshipProcess"
            })
    CompanyEvaluationSurvey findCompanyEvaluationSurveyById(Integer id);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {
                    "internshipProcess"
            })
    List<CompanyEvaluationSurvey> findAll();

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {
                    "internshipProcess"
            })
    CompanyEvaluationSurvey findByInternshipProcess(InternshipProcess internshipProcess);

    Integer countAllByInternshipProcess(InternshipProcess internshipProcess);


}
