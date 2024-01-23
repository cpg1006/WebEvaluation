package edu.sru.WebBasedEvaluations.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import edu.sru.WebBasedEvaluations.company.Province;


public interface ProvinceRepository extends CrudRepository<Province,Long>{

	
	public Province findByProvinceName(String provinceName);
	public Province findById(long id);
	public Object findAll(Sort by);
}
