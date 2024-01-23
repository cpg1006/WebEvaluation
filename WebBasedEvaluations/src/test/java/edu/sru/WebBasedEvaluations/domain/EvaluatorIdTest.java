package edu.sru.WebBasedEvaluations.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

import edu.sru.WebBasedEvaluations.domain.EvaluatorId;

@Suite
@SuiteDisplayName("Evaluator Id Methods")
@IncludeClassNamePatterns(".*Tests")
public class EvaluatorIdTest {

	static long groupIdTest = 7;
	static int levelTest = 4;
	static EvaluatorId evalId;

	@BeforeAll
	static void setup() {
		evalId = new EvaluatorId(groupIdTest, levelTest);
	}

	@Test
	public void getGroupIdTest() {
		long actual = 5;
		evalId.setGroupId(actual);
		assertEquals(evalId.getGroupId(), actual);
	}

	@Test
	public void getLevelTest() {
		int actual = 5;
		evalId.setLevel(actual);
		assertEquals(evalId.getLevel(), actual);

	}

}
