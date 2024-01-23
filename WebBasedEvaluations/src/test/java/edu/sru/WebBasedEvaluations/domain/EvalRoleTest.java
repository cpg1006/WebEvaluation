package edu.sru.WebBasedEvaluations.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;

import edu.sru.WebBasedEvaluations.company.Company;
import edu.sru.WebBasedEvaluations.domain.EvalRole;
import edu.sru.WebBasedEvaluations.domain.Evaluator;

@Suite
@SuiteDisplayName("EvalRole Methods")
@IncludeClassNamePatterns(".*Tests")
public class EvalRoleTest {

	List<Evaluator> evaluator= new ArrayList<>();
	@Mock Evaluator testEvaluator;
	static EvalRole evalRole;
	@BeforeAll
	static void setup() {
		String name = "Jeremy";
		int level = 8;
		evalRole = new EvalRole(name, level, null);
	}
	
	/*@Test
	public void getIdTest() {
		int actual = 1;
		evalRole.(actual);
		assertEquals(evalRole.getId(), actual);
	}*/
	
	@Test
	public void getNameTest() {
		String actual = "This name is not a name that is a name";
		evalRole.setName(actual);
		assertEquals(evalRole.getName(), actual);
	}
	
	@Test
	public void getEvaluatorTest() {
		Evaluator actual = testEvaluator;
		evaluator.add(testEvaluator);
		assertEquals(evaluator.get(0), actual);
		
		
	}
	
	@Test
	public void Test() {
		
	}
}
