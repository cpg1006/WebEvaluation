package edu.sru.WebBasedEvaluations.configuration;
//package edu.sru.group3.WebBasedEvaluations.configuration;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.provisioning.InMemoryUserDetailsManager;
//
//import edu.sru.group3.WebBasedEvaluations.company.Company;
//import edu.sru.group3.WebBasedEvaluations.domain.Role;
//import edu.sru.group3.WebBasedEvaluations.domain.User;
//
//@Configuration
//public class SecurityConfigTest {
//	
//	/*
//	 * This is broken, I have no idea what is wrong with this and using MyUserDetailsService
//	 * and UserDetails
//	 */
//	
////	@Bean
////	public InMemoryUserDetailsManager userDetailsService() {
////		String name = "test";
////		String firstName = "test";
////		String lastName = "test";
////		String email = "test";
////		String password = "test";
////		int employeeId = 69420;
////		String dateOfHire = "test";
////		String jobTitle = "test";
////		String supervisor = "test";
////		String divisionBranch = "test";
////		String deptName = "test";
////		Company co = new Company("test");
////		Role role = new Role();
////		UserDetails user;
////		User newUser = new User(name, firstName, lastName, email, password, 
////				employeeId, dateOfHire, jobTitle, supervisor, divisionBranch, deptName, co, role, false, false);
////		user = (UserDetails) newUser;
////		return new InMemoryUserDetailsManager(user);
////	}
//}
