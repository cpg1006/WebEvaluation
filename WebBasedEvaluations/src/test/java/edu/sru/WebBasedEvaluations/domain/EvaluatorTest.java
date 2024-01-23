package edu.sru.WebBasedEvaluations.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import edu.sru.WebBasedEvaluations.company.Company;
import edu.sru.WebBasedEvaluations.domain.EvalRole;
import edu.sru.WebBasedEvaluations.domain.EvaluationLog;
import edu.sru.WebBasedEvaluations.domain.Evaluator;
import edu.sru.WebBasedEvaluations.domain.Group;
import edu.sru.WebBasedEvaluations.domain.User;

public class EvaluatorTest {
	
	static Evaluator eval;
	List<EvaluationLog> evalLog = new ArrayList<>();
	
	@Mock static EvaluationLog log;
	@Mock static User user;
	@Mock static Group group;
	@Mock static EvalRole level;
	@Mock static Company co;
	@BeforeAll
	static void setup() {
		eval = new Evaluator(user, group, level,co);
		eval.appendEvalutationLog(log);
	}


	@Test
	public void getIdTest() {
		long actual = 5;
		eval.setId(actual);
		assertEquals(eval.getId(), actual);
	}
	
	@Test
	public void getUserTest() {
		User actual = user;
		assertEquals(eval.getUser(), actual);
	}
	
	@Test
	public void getGroupTest() {
		Group actual = group;
		assertEquals(eval.getGroup(), actual);
	}
	
	@Test
	public void getEvalutationLogTest() {
		List<EvaluationLog> actual = evalLog;
		actual.add(log);
		assertEquals(eval.getEvalutationLog(), actual);
		
	}

	@Test
	public void appendEvalutationLogTest() {
		List<EvaluationLog> actual = evalLog;
		assertNotNull(eval.getEvalutationLog());
		//Add getEvaluationLogTest Code here to add an element and test for not null and equals
	}
	
	@Test
	public void getLevelTest() {
		EvalRole actual = level;
		assertEquals(eval.getLevel(), actual);
	}
	
	@Test
	public void isSyncTest() {
		eval.setSync(false);
		assertFalse(eval.isSync());
		eval.setSync(true);
		assertTrue(eval.isSync());
		
	}
	
	@Test
	public void isPreviewTest() {
		eval.setPreview(false);
		assertFalse(eval.isPreview());
		eval.setPreview(true);
		assertTrue(eval.isPreview());
	}

	
	
}
