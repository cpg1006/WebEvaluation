package edu.sru.WebBasedEvaluations.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.Suite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import edu.sru.WebBasedEvaluations.controller.GroupController;

@Suite
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class GroupControllerTest {

	@Autowired
	GroupController controller;
	
	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;
	
	@Test
	public void SanityCheckTest() throws Exception {
		assertThat(controller).isNotNull();
	}
	
	@Test
	public void addSaveTest() throws Exception {
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/addgroup", String.class)).isNotEmpty();
	}
	
	@Test
	public void editGroupTest() throws Exception {
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/editgroup/1", String.class)).isNotEmpty();
	}
	
	@Test
	public void updateTest() throws Exception {
	
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/updategroup1", String.class)).isNotEmpty();
	}
	
	@Test
	public void uploadGroupTest() throws Exception {
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/uploadgroup", String.class)).isNotEmpty();
	}
	
	@Test
	public void uploadingGroupTest() throws Exception {
	
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/uploading", String.class)).isNotEmpty();
	}
	
	@Test
	public void evalGroups() throws Exception {
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/Evaluationgroups", String.class)).isNotEmpty();
	}
	
	@Test
	public void GroupsTest() throws Exception {
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/adminGroups", String.class)).isNotEmpty();
	}
	
	@Test
	public void deleteUserTest() throws Exception {
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/delete/1", String.class)).isNotEmpty();
	}
	
	@Test
	public void downloadEvalGroupResultsTest() throws Exception {
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/download_eval_group_results/1", String.class)).isNotEmpty();
	}
	
}
