package edu.sru.WebBasedEvaluations.domain;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;


@Service
public class EmailService {
	@Autowired
	private JavaMailSender mailSender;
	public static String selfEvaluationReviewEmailBody = "A self-Evaluation has been requested.";
	public static String selfEvaluationReviewEmailSubject = "Self-Evaluation";
	public static String deadlineReminderBody = "A deadline you are a reviewer of is due soon! Check your evaluations";
	public static String deadlineReminderSubject="Evaluation Due Soon! Check Evaluations!";
	
	public static String devFromEmail = "webeval24@gmail.com";
	public static String devToEmail = "webeval24@gmail.com";
	
	public static enum EmailType{
		SELFEVALUATION, DEADLINEREMINDER
	}
	
	public JavaMailSender getJavaMailSender() {
		JavaMailSenderImpl mSender = new JavaMailSenderImpl();
		mSender.setHost("smtp.gmail.com");
		mSender.setPort(587);
		mSender.setUsername("webeval24@gmail.com");
		mSender.setPassword("irnb ukwl hawr erqo");
		
		Properties props = mSender.getJavaMailProperties();
	    props.put("mail.transport.protocol", "smtp");
	    props.put("mail.smtp.auth", "true");
	    props.put("mail.smtp.starttls.enable", "true");
	    props.put("mail.debug", "true");
	    
	    return mSender;
	}
	
	public void sendEmail(String toEmail, String ccEmail, EmailType type) {
		
		mailSender = getJavaMailSender();
		
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
			message.setFrom(devFromEmail);
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
