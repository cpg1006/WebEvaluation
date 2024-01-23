package edu.sru.WebBasedEvaluations.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import edu.sru.WebBasedEvaluations.company.Company;
import edu.sru.WebBasedEvaluations.domain.EvalRole;
import edu.sru.WebBasedEvaluations.domain.Reviewee;

@Repository
public interface EvalRoleRepository extends CrudRepository<EvalRole,Long>{

	EvalRole findByNameAndCompany(String name,Company co);
	List<EvalRole> findByCompany(Company company);
	
	
	@Query(value= "select * from web_eval.eval_role where id= ?", nativeQuery = true)
	public EvalRole findByEvalRoleId(long id);


}
