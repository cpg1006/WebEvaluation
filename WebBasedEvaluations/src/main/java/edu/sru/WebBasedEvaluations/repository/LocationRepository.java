package edu.sru.WebBasedEvaluations.repository;

import java.util.Set;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import edu.sru.WebBasedEvaluations.company.Company;
import edu.sru.WebBasedEvaluations.company.Location;

public interface LocationRepository extends CrudRepository<Location,Long>{

	
	public Location findByLocationName(String locationName);
	public Location findByLocationNameAndCompany(String locationName,Company company);
	public Location findById(long id);
	public Object findAll(Sort by);
	public Set<Location> findByCompany(Company company);

}
