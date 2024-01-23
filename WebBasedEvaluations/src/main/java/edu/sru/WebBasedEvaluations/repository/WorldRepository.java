package edu.sru.WebBasedEvaluations.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import edu.sru.WebBasedEvaluations.company.World;


public interface WorldRepository extends CrudRepository<World,Long>{

	

	public World findById(long id);
	public Object findAll(Sort by);
	public World findByName(String name);
	
}
