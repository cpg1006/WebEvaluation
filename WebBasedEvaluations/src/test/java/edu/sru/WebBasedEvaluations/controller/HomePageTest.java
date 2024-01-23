package edu.sru.WebBasedEvaluations.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.Suite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import edu.sru.WebBasedEvaluations.controller.HomePage;

@Suite
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class HomePageTest {
	
	@Autowired
	HomePage controller;
	
	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;
	
	@Test
	public void SanityCheckTest() throws Exception {
		assertThat(controller).isNotNull();
	}
	
	@Test
	public void baseTest() throws Exception {
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/", String.class)).isNotEmpty();
	}
	
	@Test
	public void loginTest() throws Exception {
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/login", String.class)).isNotEmpty();
	}
	@Test
	public void loginLogingTest() throws Exception {
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/logging", String.class)).isNotEmpty();
	}
	@Test
	public void homeTest() throws Exception {
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/home", String.class)).isNotEmpty();
	}
	
	@Test
	public void downloadEvalExcelTest() throws Exception {
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/download_log_txt", String.class)).isNotEmpty();
	}

}
