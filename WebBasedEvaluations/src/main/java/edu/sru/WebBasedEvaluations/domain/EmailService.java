package edu.sru.WebBasedEvaluations.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class EmailService {
	@Autowired
	private JavaMailSender mailSender;
	public static String selfEvaluationReviewEmailBody = "A self-Evaluation has been requested.";
	public static String selfEvaluationReviewEmailSubject = "Self-Evaluation";
	public static String deadlineReminderBody = "A deadline you are a reminder of is due soon! Check your evaluations";
	public static String deadlineReminderSubject="Evaluation Due Soon! Check Evaluations!";
	
	public static String devFromEmail = "";
	public static String devToEmail = "";
	
	public static enum EmailType{
		SELFEVALUATION, DEADLINEREMINDER
	}
	public void sendEmail(String toEmail, String ccEmail, EmailType type) {
		
		SimpleMailMessage message = new SimpleMailMessage();
		
		switch(type) {
		case SELFEVALUATION: 
			//local testing
			message.setFrom(devFromEmail);
			message.setTo(devToEmail);
			message.setCc(devToEmail);
			
			//release version
//			message.setFrom("admin@gmail.com");
//			message.setTo(toEmail);
//			message.setCc(ccEmail);
			
			message.setText(selfEvaluationReviewEmailBody);
			message.setSubject(selfEvaluationReviewEmailSubject);
			break;
		
		case DEADLINEREMINDER:
			message.setFrom("admine@gmail.com");
			message.setTo(toEmail);
			message.setCc(ccEmail);
			
			message.setText(deadlineReminderBody);
			message.setSubject(deadlineReminderSubject);
			break;
			
			
		default:
			break;
		
		}
		
		
		mailSender.send(message);
	}

}
