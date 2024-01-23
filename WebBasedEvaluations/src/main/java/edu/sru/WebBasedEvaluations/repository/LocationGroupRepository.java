package edu.sru.WebBasedEvaluations.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import edu.sru.WebBasedEvaluations.company.Company;
import edu.sru.WebBasedEvaluations.company.LocationGroup;


public interface LocationGroupRepository extends CrudRepository<LocationGroup,Long>{

	public LocationGroup findById(long id);
	public Object findAll(Sort by);
	public LocationGroup findByCompanyAndName(Company company, String name);
	
}
