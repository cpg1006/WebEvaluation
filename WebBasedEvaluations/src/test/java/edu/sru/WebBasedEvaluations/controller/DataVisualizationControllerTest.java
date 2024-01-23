package edu.sru.WebBasedEvaluations.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.Suite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

import edu.sru.WebBasedEvaluations.controller.DataVisualizationController;
import edu.sru.WebBasedEvaluations.repository.EvalRoleRepository;
import edu.sru.WebBasedEvaluations.repository.EvaluationLogRepository;
import edu.sru.WebBasedEvaluations.repository.EvaluationRepository;
import edu.sru.WebBasedEvaluations.repository.EvaluatorRepository;
import edu.sru.WebBasedEvaluations.repository.GroupRepository;
import edu.sru.WebBasedEvaluations.repository.RevieweeRepository;
import edu.sru.WebBasedEvaluations.repository.SelfEvaluationRepository;
import edu.sru.WebBasedEvaluations.repository.UserRepository;

@Suite
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class DataVisualizationControllerTest {
	
	@Autowired
	DataVisualizationController controller;
	
	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;
	
	@Test
	public void sanityCheckTest() {
		
		assertThat(controller).isNotNull();
		
	}
	
	@Test
	public void showSQLPageTest() {
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/charts/1", String.class)).isNotEmpty();
		
	}

	@Test
	public void handlePieChartSQLTest() {
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/piechartGenerator/1", String.class)).isNotEmpty();
		
	}
	
	@Test
	public void handleRingChartSQLTest() {
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/ringchartGenerator/1", String.class)).isNotEmpty();
		
	}
	
	@Test
	public void handleBarChartTest() {
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/barchartGenerator/1", String.class)).isNotEmpty();
	}
	
	@Test
	public void handleAreaChartTest() {
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/areachartGenerator/1", String.class)).isNotEmpty();
	}
	
	@Test
	public void handlePieChartSQLIndividualTest() {
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/piechartGeneratorIndividual/1", String.class)).isNotEmpty();
	}

	public void handleRingChartSQLIndividualTest() {
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/ringchartGeneratorIndividual/1", String.class)).isNotEmpty();
	}

	@Test
	public void handleBarChartSQLIndividualTest() {
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/barchartGeneratorIndividual/1", String.class)).isNotEmpty();
	}
	
	@Test
	public void handleBarAreaSQLIndividualTest() {
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/areachartGeneratorIndividual/1", String.class)).isNotEmpty();
	}
	
	@Test
	public void handlePieChartGroupSQLTest() {
	
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/piechartGeneratorGroup/1", String.class)).isNotEmpty();
	}
	
	@Test
	public void handleRingChartGroupSQLTest() {
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/ringchartGeneratorGroup/1", String.class)).isNotEmpty();
	}
	
	@Test
	public void handleBarChartGroupSQLTest() {
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/barchartGeneratorGroup/1", String.class)).isNotEmpty();
	}
	
	@Test
	public void handleAreaChartGroupSQLTest() {
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/areachartGeneratorGroup/1", String.class)).isNotEmpty();
	}
	
	@Test
	public void handlePieChartSelfTest() {
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/piechartGeneratorSelf/1", String.class)).isNotEmpty();
	}
	
	@Test
	public void handleRingChartSelf() {
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/ringchartGeneratorSelf/1", String.class)).isNotEmpty();
	}
	
	@Test
	public void handleBarChartSelf() {
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/barchartGeneratorSelf/1", String.class)).isNotEmpty();
	}
	
	@Test
	public void handleAreaChartSelfTest() {
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/areachartGeneratorSelf/1", String.class)).isNotEmpty();
	}
	
	@Test
	public void handlePdfReportsTest() {
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/generatePdf/1", String.class)).isNotEmpty();
	}
}
