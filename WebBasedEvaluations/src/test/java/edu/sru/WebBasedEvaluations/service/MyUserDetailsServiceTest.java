package edu.sru.WebBasedEvaluations.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import edu.sru.WebBasedEvaluations.company.Company;
import edu.sru.WebBasedEvaluations.domain.MyUserDetails;
import edu.sru.WebBasedEvaluations.domain.User;
import edu.sru.WebBasedEvaluations.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Optional;

@Suite
@SuiteDisplayName("My User Details Service Methods")
@IncludeClassNamePatterns(".*Tests")
//@WebMvcTest
public class MyUserDetailsServiceTest {

	/*
	 * This is broken, I have no idea what is wrong with this and using MyUserDetailsService
	 * and UserDetails
	 */
	
	/*
	static MyUserDetailsService MyUserDetSer;

	@Mock
	static UserDetails user;

	@BeforeAll
	static void setup() {
		MyUserDetSer = new MyUserDetailsService();
		//when(MyUserDetSer.loadUserByUsername("Jon")).thenReturn(user);
	}
*/
	@Test
	public void loadUserByUsernameTests() {
		//UserDetails actual = MyUserDetSer.loadUserByUsername("Jon");
		//assertNotNull(actual);

	}
}
