package edu.sru.WebBasedEvaluations.service;

import edu.sru.WebBasedEvaluations.controller.GroupController;
import edu.sru.WebBasedEvaluations.domain.Group;
import edu.sru.WebBasedEvaluations.domain.Reviewee;
import edu.sru.WebBasedEvaluations.repository.EvaluationLogRepository;
import edu.sru.WebBasedEvaluations.repository.GroupRepository;
import edu.sru.WebBasedEvaluations.repository.RevieweeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service

public class GroupService {

    @Autowired
    EvaluationLogRepository evaluationLogRepository;

    @Autowired
    RevieweeRepository revieweeRepository;
    public void updateDeletedUser(long groupId,Reviewee review){

        try{
            evaluationLogRepository.deleteByRevieweeId(review.getId());

            // Delete reviewee by id and group_id
            revieweeRepository.deleteByIdAndGroupId(review.getId(), groupId);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    
}
