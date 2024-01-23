package edu.sru.WebBasedEvaluations.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import edu.sru.WebBasedEvaluations.company.Company;
import edu.sru.WebBasedEvaluations.domain.MyUserDetails;
import edu.sru.WebBasedEvaluations.domain.Privilege;
import edu.sru.WebBasedEvaluations.domain.Role;
import edu.sru.WebBasedEvaluations.domain.User;

public class MyUserDetailsTest{

	static User user;
	static MyUserDetails myUserDetails;
	static Role role;
	@BeforeAll
	static void setup() {
		user = new User();
		user.setEmail("Test@Test.com");
		user.setId((long) 69);
		user.setPassword("Test");
		Company co = new Company("test");
		user.setRole(new Role("Test",co));
		Role fakeRole = new Role();
		Privilege priv = new Privilege("Test", fakeRole, true, true, true, true);
		role = new Role("Test", priv, co);
		user.setRole(role);
		myUserDetails = new MyUserDetails(user);
	}
	
	//Not sure how to test this yet b/c authorities is grabbed from elsewhere
	@Test
	void getAuthoritiesTest() {
		List<GrantedAuthority> actual = new ArrayList<>();
		assertEquals(2,2);
	}

	@Test
	void getPassword() {
		String actual = "Test";
		assertEquals(myUserDetails.getPassword(), actual);
	}

	@Test
	void getIDTest() {
		long actual = 69;
		assertEquals(myUserDetails.getID(), actual);
	}

	@Test
	void getRolesTest() {
		assertEquals(myUserDetails.getRole(), role);
	}

	@Test
	void getUsernameTest() {
		String actual = "Test@Test.com";
		assertEquals(myUserDetails.getUsername(), actual);
	}

	@Test
	void isAccountNonExpiredTest() {
		assertTrue(myUserDetails.isAccountNonExpired());
	}

	@Test
	void isAccountNonLockedTest() {
		assertTrue(myUserDetails.isAccountNonLocked());
	}

	@Test
	void isCredentialsNonExpiredTest() {
		assertTrue(myUserDetails.isCredentialsNonExpired());
	}

	@Test
	void isEnabledTest() {
		assertTrue(myUserDetails.isEnabled());
	}

}
