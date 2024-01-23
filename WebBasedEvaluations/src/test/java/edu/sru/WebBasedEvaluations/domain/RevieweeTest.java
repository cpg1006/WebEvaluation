package edu.sru.WebBasedEvaluations.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import edu.sru.WebBasedEvaluations.company.Company;
import edu.sru.WebBasedEvaluations.domain.EvaluationLog;
import edu.sru.WebBasedEvaluations.domain.Group;
import edu.sru.WebBasedEvaluations.domain.Reviewee;
import edu.sru.WebBasedEvaluations.domain.SelfEvaluation;
import edu.sru.WebBasedEvaluations.domain.User;

public class RevieweeTest {
	static Reviewee revTest;
	static Company co;
	@BeforeAll
	static void setup() {
		revTest = new Reviewee();
	}

	@Test
	void getIdTest() {
		long actual = (long) 2;
		revTest.setId(actual);
		assertEquals(revTest.getId(), actual);
	}

	@Test
	void getGroupTest() {
		Group actual = new Group(co);
		revTest.setGroup(actual);
		assertEquals(revTest.getGroup(), actual);
	}

	@Test
	void getNameTest() {
		String actual = "Test";
		revTest.setName(actual);
		assertEquals(revTest.getName(), actual);
	}

	@Test
	void getUserTest() {
		User actual = new User();
		revTest.setUser(actual);
		assertEquals(revTest.getUser(), actual);
	}

	@Test
	void getEvalutationLogTest() {
		List<EvaluationLog> actual = new ArrayList<>();
		revTest.setEvalutationLog(actual);
		assertEquals(revTest.getEvalutationLog(), actual);

	}

	@Test
	void getSelfEvaluationTest() {
		SelfEvaluation actual = new SelfEvaluation();
		revTest.setSelfEvaluation(actual);
		assertEquals(revTest.getSelfEvaluation(), actual);
	}

}
