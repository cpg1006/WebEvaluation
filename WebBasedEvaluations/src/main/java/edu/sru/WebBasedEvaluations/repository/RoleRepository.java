package edu.sru.WebBasedEvaluations.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import edu.sru.WebBasedEvaluations.company.Company;
import edu.sru.WebBasedEvaluations.domain.Role;

public interface RoleRepository extends CrudRepository<Role,Long > {

	
	
	public Role findByNameAndCompany(String roleName, Company company);
	public List<Role> findRolesByNameAndCompany(String roleName, Company company);
	public List<Role> findByCompany(Company company);
	public List<Role> findByCompanyAndNameNot(Company company, String roleName);


}
