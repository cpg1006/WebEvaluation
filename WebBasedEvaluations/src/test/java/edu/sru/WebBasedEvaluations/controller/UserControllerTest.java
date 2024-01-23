package edu.sru.WebBasedEvaluations.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.Suite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import edu.sru.WebBasedEvaluations.controller.UserController;

@Suite
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UserControllerTest {
	
	@Autowired
	UserController controller;
	
	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;
	
	@Test
	public void SanityCheckTest() throws Exception {
		assertThat(controller).isNotNull();
	}
	
	@Test
	public void homeTest() throws Exception {
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/adminUsers/", String.class)).isNotEmpty();
	}
	
	@Test
	public void editSettingsTest() throws Exception {
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/userSettings/", String.class)).isNotEmpty();
	}
	
	@Test
	public void showUpdateFormTest() throws Exception {
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/edit/1/", String.class)).isNotEmpty();
	}
	
	@Test
	public void	updateUserTest() throws Exception {
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/update/1/", String.class)).isNotEmpty();
	}
	
	@Test
	public void	deleteUserTest() throws Exception {
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/delete/1/", String.class)).isNotEmpty();
	}
	
	@Test
	public void	changeUserTest() throws Exception {
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/change/1", String.class)).isNotEmpty();
	}

}
