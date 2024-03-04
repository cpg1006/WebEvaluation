package edu.sru.WebBasedEvaluations.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.sru.WebBasedEvaluations.domain.Evaluator;
import edu.sru.WebBasedEvaluations.repository.EvaluatorRepository;
import edu.sru.WebBasedEvaluations.repository.UserRepository;


@Service
public class EvaluatorService {

	 @Autowired
	    private EvaluatorRepository evaluatorRepository;
	    
	    @Autowired
	    private UserRepository userRepository;
	    
	    
	

	 @Scheduled(cron = "0 30 8 * * *")
	    @Transactional
	    public void sendEvaluationReminders() {
	    	 Calendar calendar = Calendar.getInstance();
	    	  calendar.add(Calendar.MONTH, 1);
	    	  Date newDeadline = calendar.getTime();

	    	 List<Evaluator> evaluators = (List<Evaluator>) evaluatorRepository.findAll();
	         for (Evaluator evaluator : evaluators) {
	        	 
	        	 if (evaluator.getDeadline() == null) {
	        		 evaluator.setDeadline(newDeadline);
	        		 evaluatorRepository.save(evaluator);
	        		 System.out.println("CHANGED DEADLINE OF EVALUATOR");
	        	 }		 
	         }
	 }

}


