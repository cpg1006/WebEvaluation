package edu.sru.WebBasedEvaluations;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpringWebEvalController {
	 @RequestMapping("/java4s-spring-boot-ex-tomcat")
	 public String customerInformation() {
	      return "Hey, I am from external tomcat";
	 }
}
