package edu.sru.WebBasedEvaluations.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import edu.sru.WebBasedEvaluations.company.Company;
import edu.sru.WebBasedEvaluations.domain.EvalRole;
import edu.sru.WebBasedEvaluations.domain.Evaluator;
import edu.sru.WebBasedEvaluations.domain.Group;
import edu.sru.WebBasedEvaluations.domain.User;

@Repository
public interface EvaluatorRepository extends CrudRepository<Evaluator, Long> {
	List<Evaluator> findByUser(User user);

	List<Evaluator> findByUserId(long user);

	List<Evaluator> findByGroupId(long id);

	// This method finds evaluators whose group ID is NOT the specified ID.
	List<Evaluator> findByGroupIdNot(long id);
	
	List<Evaluator> findByCompanyId(long id);
	
	//Evaluator findByEvalId(long id);

	Evaluator findById(long id);

//query to get evaluator by name
	List<Evaluator> findByUserIdAndGroupId(Long userid, long groupid);

	List<Evaluator> findByLevelLevelAndGroupId(int id, long groupid);
	List<Evaluator> findByLevelLevelAndGroupNumberAndCompany(int id, int number, Company company);

	void deleteByGroupId(long id);

	Boolean existsBylevelAndGroup(EvalRole evalRole, Group group);
	
	@Transactional
	@Modifying
	@Query("DELETE FROM Evaluator WHERE id = :eid and group_id = :gid")
	void deleteByIdAndGroupId(long eid, long gid);
	
//	@Query("Select Distinct user_id from Evaluator where 'company' = :companyE ")
//	List<Long> findDistinctByCompanyId(Company companyE);

}
