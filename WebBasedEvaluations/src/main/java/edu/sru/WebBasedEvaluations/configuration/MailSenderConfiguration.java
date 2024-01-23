package edu.sru.WebBasedEvaluations.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/*
 * Attempt to address the fieldMail sender error upon running the code
 * This code snippet was taken from https://stackoverflow.com/questions/57093656/a-bean-of-type-org-springframework-mail-javamail-javamailsender-that-could-not
 */

@Configuration
public class MailSenderConfiguration {
	 @Bean
	    public JavaMailSender javaMailSender() {
	        return new JavaMailSenderImpl();
	    }
}
