package edu.sru.WebBasedEvaluations.repository;



import org.springframework.data.repository.CrudRepository;

import edu.sru.WebBasedEvaluations.domain.Privilege;

public interface PrivilegeRepository extends CrudRepository<Privilege,Long>{
	
	public Privilege findByName(String name);
}
