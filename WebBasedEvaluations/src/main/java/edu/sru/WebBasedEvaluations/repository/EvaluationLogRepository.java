package edu.sru.WebBasedEvaluations.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import edu.sru.WebBasedEvaluations.company.Company;
import edu.sru.WebBasedEvaluations.domain.EvaluationLog;
import edu.sru.WebBasedEvaluations.domain.Evaluator;
import edu.sru.WebBasedEvaluations.domain.EvaluatorId;
import edu.sru.WebBasedEvaluations.domain.Group;
import edu.sru.WebBasedEvaluations.domain.Reviewee;
import org.springframework.transaction.annotation.Transactional;


public interface EvaluationLogRepository extends CrudRepository<EvaluationLog,Long > {
	
	@Query("Select path from EvaluationLog where reviewee.id = ?1")
	Iterable<EvaluationLog> findByReviewee(long id);
	
	@Query("SELECT path FROM EvaluationLog where path is not null\n"
			+ "and reviewee.id = ?1")
	Iterable<EvaluationLog> findByIdNotNull(Long id);
	
	
	List<EvaluationLog> findByevaluator(Evaluator eval);

	EvaluationLog findByEvaluatorAndReviewee(Evaluator evaluator, Reviewee rev);

	
	EvaluationLog findByEvaluatorId(EvaluatorId evaluator);
	
	List<EvaluationLog>  findByEvaluatorCompany(Company company);

	EvaluationLog findByEvaluatorIdAndRevieweeId(long evalid, long revid);

	@Transactional
	@Modifying
	@Query("DELETE FROM EvaluationLog WHERE reviewee.id = :revieweeId")
	void deleteByRevieweeId(Long revieweeId);
	
	@Transactional
	@Modifying
	@Query("DELETE FROM EvaluationLog WHERE evaluator.id = :evalId")
	void deleteByEvaluatorId(long evalId);


	//dvoid deleteAllByGroup(Group group);
	

	//EvaluationLog findByevaluatorAndreviewee(Evaluator evaluator, Reviewee rev);
}
