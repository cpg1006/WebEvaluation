package edu.sru.WebBasedEvaluations.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import edu.sru.WebBasedEvaluations.company.Country;

public interface CountryRepository extends CrudRepository<Country,Long>{

	
	public Country findByCountryName(String countryName);
	public Country findById(long id);
	public Object findAll(Sort by);
	
	
}
