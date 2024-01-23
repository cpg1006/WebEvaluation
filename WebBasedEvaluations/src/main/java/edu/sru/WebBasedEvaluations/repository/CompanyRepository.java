package edu.sru.WebBasedEvaluations.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import edu.sru.WebBasedEvaluations.company.Company;


@Repository
public interface CompanyRepository extends CrudRepository<Company,Long>{

	public Company findByCompanyName(String companyName);
	public Company findById(long id);
	public Object findAll(Sort by);
	public List<Company> findAll();

}
