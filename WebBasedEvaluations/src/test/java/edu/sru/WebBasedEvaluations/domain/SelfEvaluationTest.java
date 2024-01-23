package edu.sru.WebBasedEvaluations.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import edu.sru.WebBasedEvaluations.domain.Reviewee;
import edu.sru.WebBasedEvaluations.domain.SelfEvaluation;

public class SelfEvaluationTest {

	static SelfEvaluation selfEval;

	@BeforeAll
	static void setup() {
		selfEval = new SelfEvaluation();
	}

	@Test
	void getRevieweeTest() {
		Reviewee actual = new Reviewee();
		selfEval.setReviewee(actual);
		assertEquals(selfEval.getReviewee(), actual);
	}

	@Test
	void getPathTest() {
		byte[] actual = new byte[1];
		actual[0] = (byte) 4;
		selfEval.setPath(actual);
		assertEquals(selfEval.getPath(), actual);
	}

	@Test
	void getIdTest() {
		long actual = (long) 6;
		selfEval.setId(actual);
		assertEquals(selfEval.getId(), actual);
	}

	@Test
	void getDateEdited() {
		Date actual = new Date();
		selfEval.setDateEdited(actual);
		assertEquals(selfEval.getDateEdited(), actual);
	}

	@Test
	void getCompleted() {
		selfEval.setCompleted(true);
		assertTrue(selfEval.getCompleted());
		selfEval.setCompleted(false);
		assertFalse(selfEval.getCompleted());
	}

}
