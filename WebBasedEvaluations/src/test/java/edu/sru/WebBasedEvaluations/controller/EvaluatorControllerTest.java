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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.sru.WebBasedEvaluations.controller.EvaluatorController;
import edu.sru.WebBasedEvaluations.evalform.Evaluation;
import edu.sru.WebBasedEvaluations.repository.EvalRoleRepository;
import edu.sru.WebBasedEvaluations.repository.EvaluationLogRepository;
import edu.sru.WebBasedEvaluations.repository.EvaluationRepository;
import edu.sru.WebBasedEvaluations.repository.EvaluatorRepository;
import edu.sru.WebBasedEvaluations.repository.RevieweeRepository;
import edu.sru.WebBasedEvaluations.repository.UserRepository;

@Suite
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class EvaluatorControllerTest {
	
	@Autowired
	EvaluatorController controller;
	
	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;
	
	@Test
	public void SanityCheckTest() throws Exception {
		assertThat(controller).isNotNull();
	}

	@Test
	public void submitEvalTest() {
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/eval/1", String.class)).isNotEmpty();
	}

	@Test
	public void previewEvalTest() {
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/previeweval/1", String.class)).isNotEmpty();
	}

	@Test
	public void downloadTest() {
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/downloada/1", String.class)).isNotEmpty();
	}

	@Test
	public void saveEvalFormTest() {
		
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/save_eval/1", String.class)).isNotEmpty();
	}
}
