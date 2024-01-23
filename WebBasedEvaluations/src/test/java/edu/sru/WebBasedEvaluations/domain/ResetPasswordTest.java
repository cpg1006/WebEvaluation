package edu.sru.WebBasedEvaluations.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import edu.sru.WebBasedEvaluations.domain.ResetPassword;

public class ResetPasswordTest {
	static ResetPassword resetPass;

	@BeforeAll
	static void setup() {
		resetPass = new ResetPassword();
	}

	@Test
	void getEmailTest() {
		String actual = "Test";
		resetPass.setEmail(actual);
		assertEquals(resetPass.getEmail(), actual);
	}

	@Test
	void getPasswordTest() {
		String actual = "Test";
		resetPass.setPassword(actual);
		assertEquals(resetPass.getPassword(), actual);
	}

	@Test
	void getPasswordCheckTest() {
		String actual = "Test";
		resetPass.setPasswordCheck(actual);
		assertEquals(resetPass.getPasswordCheck(), actual);
	}

}