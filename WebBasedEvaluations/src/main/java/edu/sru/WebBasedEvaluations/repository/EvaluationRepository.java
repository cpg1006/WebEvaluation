package edu.sru.WebBasedEvaluations.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import edu.sru.WebBasedEvaluations.company.Company;
import edu.sru.WebBasedEvaluations.domain.EvalRole;
import edu.sru.WebBasedEvaluations.domain.EvalTemplates;


public interface EvaluationRepository extends CrudRepository<EvalTemplates,String > {
    List<EvalTemplates> findByNameIn(List<String> names);
    List<EvalTemplates> findByCompany(Company company);
    long count();
	EvalTemplates findByNameAndCompany(String name, Company company);
    EvalTemplates findById(long id);
	



}
