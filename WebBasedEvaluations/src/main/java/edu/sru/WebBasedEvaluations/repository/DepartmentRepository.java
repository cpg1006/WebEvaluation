package edu.sru.WebBasedEvaluations.repository;

import java.util.Set;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import edu.sru.WebBasedEvaluations.company.Company;
import edu.sru.WebBasedEvaluations.company.Department;


public interface DepartmentRepository extends CrudRepository<Department,Long>{
	
	public Department findByNameAndCompany(String name, Company company);
	public Department findById(long id);
	public Object findAll(Sort by);
	public Set<Department> findByCompany(Company company);
	
	@Query(value= "SELECT * FROM web_eval.department where name=? and company_id=?", nativeQuery = true)
	long getDepartmentId(Department dept, long companyId);
	
	
}


