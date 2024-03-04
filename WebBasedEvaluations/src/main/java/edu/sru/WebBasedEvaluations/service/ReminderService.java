package edu.sru.WebBasedEvaluations.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import edu.sru.WebBasedEvaluations.domain.EmailService;
import edu.sru.WebBasedEvaluations.domain.Evaluator;
import edu.sru.WebBasedEvaluations.repository.EvaluatorRepository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class ReminderService {

    @Autowired
    private EvaluatorRepository evaluatorRepository;

    @Autowired
    private EmailService emailService;

    // This method will be executed every day at 8 AM (customize the cron expression as needed)
    @Scheduled(cron = "0 0 8 * * ?")
    public void sendEvaluationReminders() {
    	 Calendar calendar = Calendar.getInstance();
         Date today = calendar.getTime();

         calendar.add(Calendar.DAY_OF_YEAR, 7); // Setting as reminder for one week before deadline
         Date nextWeek = calendar.getTime();

         List<Evaluator> evaluators = evaluatorRepository.findByDeadlineBetween(today, nextWeek);
         for (Evaluator evaluator : evaluators) {
        	    emailService.sendEmail(evaluator.getUser().getEmail(), "", EmailService.EmailType.DEADLINEREMINDER);
            }
        }
    }