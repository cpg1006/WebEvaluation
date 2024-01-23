package edu.sru.WebBasedEvaluations.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.Suite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import edu.sru.WebBasedEvaluations.controller.EvalFormController;
import edu.sru.WebBasedEvaluations.evalform.Evaluation;
import edu.sru.WebBasedEvaluations.repository.EvaluationLogRepository;
import edu.sru.WebBasedEvaluations.repository.EvaluationRepository;
import edu.sru.WebBasedEvaluations.repository.GroupRepository;

@Suite
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class EvalFormControllerTest {
	
	@Autowired
	EvalFormController controller;
	
	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;
	
	@Test
	public void SanityCheckTest() throws Exception {
		assertThat(controller).isNotNull();
	}
	
	@Test
	public void adminEvaluationsTest() {
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/adminEvaluations", String.class)).isNotEmpty();
	}

	@Test
	public void uploadEvalTemplateTest() {
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/upload_eval", String.class)).isNotEmpty();
	}

	@Test
	public void saveEvalTemplateTest() {
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/eval_form", String.class)).isNotEmpty();
	}

	@Test
	public void downloadEvalResultsTest() {
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/download_eval_results/1", String.class)).isNotEmpty();
	}

	@Test
	public void downloadEvalExcelTest() {
		
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/download_eval_excel/1", String.class)).isNotEmpty();
	}

	@Test
	public void downloadErrorLogTest() {
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/dl_error_log/1", String.class)).isNotEmpty();
	}

	@Test
	public void deleteEvalTemplateTest() {
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/del_eval_form/1", String.class)).isNotEmpty();
	}
}
