package edu.sru.WebBasedEvaluations.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.sru.WebBasedEvaluations.domain.EmailService;
import edu.sru.WebBasedEvaluations.domain.Evaluator;
import edu.sru.WebBasedEvaluations.domain.User;
import edu.sru.WebBasedEvaluations.repository.EvaluatorRepository;
import edu.sru.WebBasedEvaluations.repository.UserRepository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class ReminderService {

    @Autowired
    private EvaluatorRepository evaluatorRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    // This method will be executed every day at 8 AM
    @Scheduled(cron = "0 0 8 * * *")
    @Transactional
    public void sendEvaluationReminders() {
    	 Calendar calendar = Calendar.getInstance();
         Date today = calendar.getTime();


         calendar.add(Calendar.DAY_OF_YEAR, 7); // Setting as reminder for one week before deadline
         Date nextWeek = calendar.getTime();

         List<Evaluator> evaluators = evaluatorRepository.findByDeadlineBetween(today, nextWeek);
         for (Evaluator evaluator : evaluators) {
        	 
        	 if (evaluator.getDeadline() != null && evaluator.getUser() != null) {
        		 String ccEmail ="admine@gmail.com";
        		 String supervisorName = evaluator.getUser().getSupervisor();
        		 
        		 if (supervisorName != null) {
                     // Attempt to find the supervisor by name and get their email
                     User supervisor = userRepository.findByName(supervisorName);
                     
                     if (supervisor != null) {
                         ccEmail = supervisor.getEmail();
                     }
                 }
        	    emailService.sendEmail(evaluator.getUser().getEmail(), ccEmail, EmailService.EmailType.DEADLINEREMINDER);
        	 }
         }
     }
 }
   