package edu.sru.WebBasedEvaluations.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.Suite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import edu.sru.WebBasedEvaluations.controller.SelfEvaluationController;

@Suite
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SelfEvaluationControllerTest {
	
	@Autowired
	SelfEvaluationController controller;
	
	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;
	
	@Test
	public void SanityCheckTest() throws Exception {
		assertThat(controller).isNotNull();
	}
	
	@Test
	public void submitSelfEvalTest() throws Exception {
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/selfeval/1", String.class)).isNotEmpty();
	}
	
	@Test
	public void viewSelfEvalTest() throws Exception {
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/viewselfeval/1/\"User\"", String.class)).isNotEmpty();
	}
	
	@Test
	public void saveSelfEvalForm() throws Exception {
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/save_selfeval/1", String.class)).isNotEmpty();
	}

}
