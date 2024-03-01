package edu.sru.WebBasedEvaluations.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
	@Autowired
	private JavaMailSender mailSender;
	
	public void sendEmail(String toEmail, String emailSubject, String emailBody) {
		
		SimpleMailMessage message = new SimpleMailMessage();
		
		message.setFrom("admin@gmail.com");
		message.setTo(toEmail);
		message.setText(emailBody);
		message.setSubject(emailSubject);
		
		mailSender.send(message);
	}

}
