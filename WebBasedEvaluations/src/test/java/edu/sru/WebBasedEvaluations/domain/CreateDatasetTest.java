
package edu.sru.WebBasedEvaluations.domain;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.junit.jupiter.api.BeforeAll;

import edu.sru.WebBasedEvaluations.domain.CreateDataset;
import edu.sru.WebBasedEvaluations.evalform.Evaluation;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;
import org.mockito.Mock;



import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;
import org.mockito.Mock;

@Suite
@SuiteDisplayName("Create Dataset Methods")
@IncludeClassNamePatterns(".*Tests")
public class CreateDatasetTest {

	static Evaluation testEvaluation;
	static List<Evaluation> completedEvals;
	static CreateDataset createDataset;
	@BeforeAll
	static void setup() {
		createDataset = new CreateDataset();
		testEvaluation = new Evaluation("Test");
	}
	
	@Test
	void createTestPieDatasetTest() {
		DefaultPieDataset expected = createDataset.createTestPieDataset();
		DefaultPieDataset actual = new DefaultPieDataset();
		actual.setValue("Male", 10);
		actual.setValue("Female", 5);
		if (expected.getItemCount() != actual.getItemCount()) {
			assertTrue(false);
		}
		for (int i = 0; i < expected.getItemCount() && i < actual.getItemCount(); i++) {
			assertEquals(expected.getValue(i), actual.getValue(i));
		}
		assertEquals(expected, actual);
	}
	
	@Test
	void createTestDatasetTest() {
		DefaultCategoryDataset expected = createDataset.createTestDataset();
		DefaultCategoryDataset actual = new DefaultCategoryDataset();
		actual.setValue(10, "test", "test");
		actual.setValue(5, "test 2", "test 2");
		assertEquals(expected, actual);
	}
	
	@Test
	public void createPieDatasetTest() {
		/*
		completedEvals = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			completedEvals.add(testEvaluation);
		}
		completedEvals.add(testEvaluation);
		completedEvals.set(0, testEvaluation);
		DefaultPieDataset actual = new DefaultPieDataset();
		
		DefaultPieDataset expected = createDataset.createPieDataset(completedEvals);
		assertEquals(expected, actual);
		*/
	}
	
	@Test
	public void createDefaultDatasetTest() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
	}
	
	@Test
	public void createPieDatasetIndividualTest() {
		DefaultPieDataset actual = new DefaultPieDataset();
	}
	
	@Test
	public void createDefaultDatasetIndividualTest() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
	}
	
	@Test
	public void createPieDatasetGroupTest() {
		DefaultPieDataset actual = new DefaultPieDataset();
	}
	
	@Test
	public void createDefaultDatasetGroupTest() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
	}
	
	@Test
	public void createPieDatasetSelfTest() {
		DefaultPieDataset actual = new DefaultPieDataset();
	}
	
	@Test
	public void Test() {
		
	}
}

