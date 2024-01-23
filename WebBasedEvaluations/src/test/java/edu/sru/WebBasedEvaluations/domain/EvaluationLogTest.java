package edu.sru.WebBasedEvaluations.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;
import org.mockito.Mock;

import edu.sru.WebBasedEvaluations.domain.EvaluationLog;
import edu.sru.WebBasedEvaluations.domain.Evaluator;
import edu.sru.WebBasedEvaluations.domain.Reviewee;
import edu.sru.WebBasedEvaluations.domain.User;


@Suite
@SuiteDisplayName("Evaluation Log Methods")
@IncludeClassNamePatterns(".*Tests")
public class EvaluationLogTest {

	@Mock User user;
	
	static EvaluationLog evalLog;
	@BeforeAll
	static void setup() {
		evalLog = new EvaluationLog();

	}

	@Test
	public void getEvaluatorTest() {
		Evaluator actual = new Evaluator();
		actual.setUser(user);
		evalLog.setEvaluator(actual);
		assertEquals(evalLog.getEvaluator(), actual);
	}

	@Test
	public void getRevieweeTest() {
		Reviewee actual = new Reviewee();
		actual.setName("Joe");
		evalLog.setReviewee(actual);
		assertEquals(evalLog.getReviewee(), actual);
	}

	@Test
	public void getPathTest() {
		byte[] actual = new byte[1];
		actual[0] = (byte) 4;
		evalLog.setPath(actual);
		assertEquals(evalLog.getPath(), actual);
	}

	@Test
	public void getId() {
		long actual = 3;
		evalLog.setId(actual);
		assertEquals(evalLog.getId(), actual);
	}

	@Test
	public void getDateEdited() {
		Date actual = new Date();
		evalLog.setDateEdited(actual);
		assertEquals(evalLog.getDateEdited(), actual);
	}

	@Test
	public void getAuthTest() {
		evalLog.setAuth(false);
		assertFalse(evalLog.getAuth());
		evalLog.setAuth(true);
		assertTrue(evalLog.getAuth());
	}

	@Test
	public void getCompletedTest() {
		evalLog.setCompleted(true);
		assertTrue(evalLog.getCompleted());
		evalLog.setCompleted(false);
		assertFalse(evalLog.getCompleted());
	}

	@Test
	public void getAttachTest() {
		byte[] actual = new byte[1];
		actual[0] = (byte) 4;
		evalLog.setAttach(actual);
		assertEquals(evalLog.getAttach(), actual);
	}


	public void getAttachnameTest() {
		String actual = "attachment";
		evalLog.setAttachname(actual);
		assertEquals(evalLog.getAttachname(), actual);
	}


}
