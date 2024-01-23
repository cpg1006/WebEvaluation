package edu.sru.WebBasedEvaluations.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.Suite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import edu.sru.WebBasedEvaluations.controller.ArchiveController;

import org.springframework.boot.test.context.SpringBootTest;

@Suite
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ArchiveControllerTest {
	
	@Autowired
	ArchiveController controller;
	
	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;
	
	@Test
	public void sanityCheckTest() throws Exception {
		assertThat(controller).isNotNull();
	}
	
	@Test
	public void evalGroupsTest() throws Exception {
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/Archivegroups", String.class)).isNotEmpty();
	}
	
	@Test
	public void ViewViewArchiveTest() throws Exception {
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/ViewArchive/1", String.class)).isNotEmpty();
	}
		
		
		

}
