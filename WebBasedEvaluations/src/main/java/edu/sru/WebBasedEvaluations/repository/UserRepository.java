package edu.sru.WebBasedEvaluations.repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import edu.sru.WebBasedEvaluations.company.Company;
import edu.sru.WebBasedEvaluations.domain.User;
/**
 * @author Tanuj Rane, Dalton Stenzel
 *
 */
@Repository
public interface UserRepository extends CrudRepository<User,Long>{

	
	public User findByEmail(String email);
	public User findByName(String name);
	public User findUserByEmail(String email);
	public User findByFirstName(String firstName);
	public User findByLastName(String lastName);
	public User findByCompanyName(String companyName);
	public User findByid(long l);
	
	public List<User> findByRoleId(long id);

	public List<User> findByRoleNameEquals(String name);

	public List<User> findByRoleNameEqualsOrRoleNameEquals(String name1, String name2);

	@Query(value= "select * from user u where ?2 = u.company_id AND (u.first_name like %?1% or u.email like %?1% or u.last_name like %?1%)", nativeQuery = true)
	List<User> findByKeywordAndCompany(String keyword, Long companyID);
	
	//public User findById(long id);
	public Object findAll(Sort by);
	List<User> findByCompany(Company company);
//	public List<User>findByRoleName(String name, String name2,Sort by);

/*
 * this version uses the company ID to serve up users as well as the fname/lname/email of the user. 
 * 
 * public User findByEmail(String email, Long companyID);
	public User findByName(String name, Long companyID);
	public User findByFirstName(String firstName, Long companyID);
	public User findByLastName(String lastName, Long companyID);
	public User findByCompanyName(String companyName, Long companyID);


	public User findByid(long l);
	@Query(value= "select * from user u where u.first_name like %:keyword% or u.email like %:keyword% or u.last_name like %:keyword% and u.company_id = %:companyID%", nativeQuery = true)
	List<User> findByKeyword(@Param("keyword") String keyword, @Param("companyID") Long companyID);
	//public User findById(long id);
	public Object findAll(Sort by);
	public List<User>findByRolesOrRoles(String name, String name2,Sort by);
 */





}
