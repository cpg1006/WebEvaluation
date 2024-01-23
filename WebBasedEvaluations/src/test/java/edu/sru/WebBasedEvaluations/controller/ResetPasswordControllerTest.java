package edu.sru.WebBasedEvaluations.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.Suite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.ui.Model;

import edu.sru.WebBasedEvaluations.controller.ResetPasswordController;

@Suite
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ResetPasswordControllerTest {

	@Autowired
	ResetPasswordController controller;
	
	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;
	
	@Test
	public void SanityCheckTest() throws Exception {
		assertThat(controller).isNotNull();
	}
	
	@Test
	public void resetPasswordTest() throws Exception {
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/resetting", String.class)).isNotEmpty();
	}
	
	@Test
	public void firstResetPasswordTest() throws Exception {
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/firstReset", String.class)).isNotEmpty();
	}
	
	@Test
	public void resetSentTest() throws Exception {
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/reset", String.class)).isNotEmpty();
	}
	
	@Test
	public void showChangePasswordPageTest() throws Exception {
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/changePassword", String.class)).isNotEmpty();
	}
	
	@Test
	public void updateRecoveryTest() throws Exception {
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port +  "/recoverChanges/1", String.class)).isNotEmpty();
	}
	
	@Test
	public void emailSentConfirmTest() throws Exception {
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/email_sent", String.class)).isNotEmpty();
	}
	
}
