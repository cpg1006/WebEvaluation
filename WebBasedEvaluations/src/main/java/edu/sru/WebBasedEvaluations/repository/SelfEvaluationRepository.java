package edu.sru.WebBasedEvaluations.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import edu.sru.WebBasedEvaluations.domain.EvalTemplates;
import edu.sru.WebBasedEvaluations.domain.Reviewee;
import edu.sru.WebBasedEvaluations.domain.SelfEvaluation;
import edu.sru.WebBasedEvaluations.domain.User;
@Repository
public interface SelfEvaluationRepository extends  CrudRepository<SelfEvaluation,Long>{
	


	SelfEvaluation findByRevieweeUser_Id(long id);

	SelfEvaluation findByReviewee(Reviewee reviewee);

}
