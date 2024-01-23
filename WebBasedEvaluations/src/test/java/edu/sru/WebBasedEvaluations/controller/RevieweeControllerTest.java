package edu.sru.WebBasedEvaluations.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.Suite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import edu.sru.WebBasedEvaluations.controller.RevieweeController;

@Suite
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class RevieweeControllerTest {
	
	@Autowired
	RevieweeController controller;
	
	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;
	
	@Test
	public void SanityCheckTest() throws Exception {
		assertThat(controller).isNotNull();
	}

	@Test
	public void	getrevieweeTest() throws Exception {
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/myEval", String.class)).isNotEmpty();
	}
	
	@Test
	public void getrevieweegrouptest() throws Exception {
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/admineval/1", String.class)).isNotEmpty();
	}
	
}
