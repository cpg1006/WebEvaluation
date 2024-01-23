package edu.sru.WebBasedEvaluations.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.*;

import edu.sru.WebBasedEvaluations.company.Company;
import edu.sru.WebBasedEvaluations.domain.Group;
import edu.sru.WebBasedEvaluations.domain.Reviewee;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface RevieweeRepository extends CrudRepository<Reviewee,Long >{

	@Query(value = "SELECT name from web_eval.reviewee where id = ?", nativeQuery = true)
	String findNameById(long ID);
	
	@Query(value = "SELECT group_id from web_eval.reviewee where id = ? ", nativeQuery = true)
	String findGroupById(long ID);


	@Query("SELECT r FROM Reviewee r WHERE r.group.id = :groupId AND r.user.id = :userId")
	Optional<Reviewee> findByGroupIdAndUserId(Long groupId, Long userId);

	List<Reviewee>findByCompany(Company company);
	
	List<Reviewee>findBygroup_Id(long ID);

	List<Reviewee> findBygroup(Group group);

	List<Reviewee> findByUser_Id(long l);
	
	Reviewee findById(long id);

	Reviewee findByNameAndCompany(String string, Company company);

	List<Reviewee> findByuser_Id(long id);
	
	List<Reviewee> findByGroupNumberAndCompany(int groupNum, Company company);
	
	@Query(value = "SELECT * FROM web_eval.reviewee where user_id= ? and group_id= ?", nativeQuery = true)
	Reviewee findByUserIdGroupId(long uid, long gid);


	@Transactional
	@Modifying
	@Query("DELETE FROM Reviewee WHERE id = :revieweeId AND group.id = :groupId")
	void deleteByIdAndGroupId(Long revieweeId, Long groupId);
	
}
