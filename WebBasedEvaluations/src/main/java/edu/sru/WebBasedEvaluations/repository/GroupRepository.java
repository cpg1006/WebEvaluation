package edu.sru.WebBasedEvaluations.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import edu.sru.WebBasedEvaluations.domain.Reviewee;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.*;

import edu.sru.WebBasedEvaluations.company.Company;
import edu.sru.WebBasedEvaluations.domain.Evaluator;
import edu.sru.WebBasedEvaluations.domain.Group;
import edu.sru.WebBasedEvaluations.domain.User;

@Repository
public interface GroupRepository extends CrudRepository<Group,Long > {

	List<Group> findByevaluatorUserId(long ID);
	Group findById(long ID);
	Group findByGroupName(String GroupName);
	Group findByNumberAndCompany(int number, Company company);
	List<Group> findByCompany(Company company);
	//EvaluatorRepository evaluatorRepository = ;
	//List<Group>findByEvaluator(Evaluator evaluator);
	//void removeAll(List<Group> grouplist);
	List<Group> findByevaluatorUserId(long id, Sort by);
	
	
	@Query(value= "SELECT COUNT(*) FROM web_eval.reviewee WHERE group_id = ?", nativeQuery = true)
	int getGroupSize(long id);
	
	List<Group> findAll();

	@Query(value= "SELECT * FROM web_eval.groupeval where archived=0 ORDER BY CAST(SUBSTRING_INDEX(group_name, ' ', -1) AS SIGNED), group_name", nativeQuery = true)
	List<Group> findAllByOrderByGroupNameAsc();

	List<Group> findAll(Pageable pageable);

	ArrayList<Group> findGroupsByGroupNameAndYear(String groupName, int year);
}
