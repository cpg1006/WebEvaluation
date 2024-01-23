package edu.sru.WebBasedEvaluations.EvalForm;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;

import edu.sru.WebBasedEvaluations.evalform.ComputeRange;
import edu.sru.WebBasedEvaluations.evalform.Evaluation;
import edu.sru.WebBasedEvaluations.evalform.Section;

@Suite
@SpringBootTest
public class EvaluationTest {
	
	static String evalID;
	static String title;
	static String description;
	
	static String warning;
	static String error;
	static String group;
	
	static Section section;
	static ComputeRange range;

	static List <Section> sectionList;
	static List <String> warnings;
	static List <String> errors;
	static List <String> groupsList;
	static List <ComputeRange> computeRanges;
	
	static Evaluation eval;
	
	@BeforeAll
	static void begin() {
		
		evalID = "eval";
		title = "Test Title";
		description = "Bla Bla Bla";
		
		warning = "warn";
		error = "E";
		group = "we be chillin";
		
		sectionList = new ArrayList<Section>();
		warnings = new ArrayList<String>();
		errors = new ArrayList<String>();
		groupsList = new ArrayList<String>();
		computeRanges = new ArrayList<ComputeRange>();
		
		section = new Section();
		range = new ComputeRange();
		
		sectionList.add(section);
		warnings.add(warning);
		errors.add(error);
		groupsList.add(group);
		computeRanges.add(range);
		
		eval = new Evaluation();
		
		eval.setComputeRanges(computeRanges);
		eval.setDescription(description);
		eval.setEvalID(evalID);
		eval.setTitle(title);
		
		eval.addGroup(group);
		eval.addComputeRange(range);
		eval.addError(error);
		eval.addSection(section);
		eval.addWarning(warning);
		
	}
	
	@Test
	public void getEvalID() {
		
		eval.setEvalID(evalID);
		
		assertTrue(eval.getEvalID() == evalID);
		
	}
	
	@Test
	public void setEvalID() {
		
		String newId = "I'm an Id";
		
		eval.setEvalID(newId);
		
		assertTrue(eval.getEvalID() == newId);
		
	}
	
	@Test
	public void getTitle() {
		
		eval.setTitle(title);
		
		assertTrue(eval.getTitle() == title);
		
	}
	
	@Test
	public void setTitle() {
		
		String title2 = "Unga";
		
		eval.setTitle(title2);
		
		assertTrue(eval.getTitle() == title2);
		
	}
	
	@Test
	public void getDescription() {
		
		eval.setDescription(description);
		
		assertTrue(eval.getDescription() == description);
		
	}
	
	@Test
	public void setDescription() {
		
		String des = "I'm a des";
		
		eval.setDescription(des);
		
		assertTrue(eval.getDescription() == des);

		
	}
	
	@Test
	public void getCompleted() {
		
		assertTrue(eval.getCompleted() == false);
		
	}
	
	@Test
	public void setCompleted() {
		
		boolean TRUE = true;
		
		eval.setCompleted(TRUE);
		
		assertTrue(eval.getCompleted() == TRUE);
		
	}
	
	@Test
	public void getComputeTotals() {
		
		assertTrue(eval.getComputeTotals() == false);
		
	}
	
	@Test
	public void setComputeTotals() {
		
		boolean TRUE = true;
		
		eval.setComputeTotals(TRUE);
		
		assertTrue(eval.getComputeTotals() == TRUE);
		
		
	}
	
	@Test
	public void getGroupsList() {
		
		List <String> CheckList = new ArrayList<String>();
		
		CheckList = eval.getGroupsList();
		
		assertTrue(eval.getGroupsList() == CheckList);
		
	}
	
	@Test
	public void addGroup() {
		
		group = "G2";
		
		eval.addGroup(group);
		groupsList.add(group);
		
		List <String> CheckList = new ArrayList<String>();
		
		CheckList = eval.getGroupsList();
		
		//This test is doomed to fail because it's not checkable
		assertTrue(eval.getGroupsList() == CheckList);
		
	}
	
	@Test
	public void clearGroupsList() {
		
		groupsList.clear();
		eval.clearGroupsList();
		
		assertTrue(eval.getGroupsList().isEmpty());
	}
	
	@Test
	public void addError() {
		
		error = "E2";
		
		eval.addError(error);
		errors.add(error);
		
		assertTrue(eval.getError(eval.getErrorCount() - 1) == errors.get(errors.size() -1));
		
		
	}
	
	@Test
	public void getError() {
		
		assertTrue(eval.getError(0) == errors.get(0));
		
	}
	
	@Test
	public void getErrorCount() {
		
		assertTrue(eval.getErrorCount() == errors.size());
		
	}
	
	@Test
	public void getErrors() {
		
		int i = 0;
		
		while(i < errors.size()) {
			
			assertTrue(eval.getError(i) == errors.get(i));
			i++;
			
		}
		
	}
	
	@Test
	public void addWarning() {
		
		warning = "W2";
		
		eval.addWarning(warning);
		warnings.add(warning);
		
		assertTrue(eval.getWarning(eval.getWarningCount() - 1) == warnings.get(warnings.size() -1));
		
	}
	
	@Test
	public void getWarning() {
		
		assertTrue(eval.getWarning(0) == warnings.get(0));
		
	}
	
	@Test
	public void getWarningCount() {
		
		assertTrue(eval.getWarningCount() == warnings.size());
		
	}
	
	@Test
	public void getWarnings() {
		
		int i = 0;
		
		while(i < warnings.size()) {
			
			assertTrue(eval.getWarning(i) == warnings.get(i));
			i++;
			
		}
		
	}
	
	@Test
	public void addSection() {
		
		section = new Section();
		
		sectionList.add(section);
		eval.addSection(section);
		
		assertTrue(eval.getSection(eval.getSectionCount() - 1) == sectionList.get(sectionList.size() - 1));
		
	}
	
	@Test
	public void getSection() {
		
		assertTrue(eval.getSection(0) == sectionList.get(0));
		
	}
	
	@Test
	public void getSectionCount() {
		
		assertTrue(eval.getSectionCount() == sectionList.size());
		
	}
	
	@Test
	public void getSections() {
		
		int i = 0;
		
		while(i < sectionList.size()) {
			
			assertTrue(eval.getSection(i) == sectionList.get(i));
			i++;
			
		}
		
	}
	
	@Test
	public void setComputeRanges() {
		
		eval.setComputeRanges(computeRanges);
		
		assertTrue(eval.getComputeRanges() == computeRanges);
		
	}
	
	@Test
	public void addComputeRange() {
		
		range = new ComputeRange();
		
		computeRanges.add(range);
		eval.addComputeRange(range);
		
		assertTrue(eval.getComputeRanges() == computeRanges);
		
	}
	
	@Test
	public void getComputeRange() {
		
		assertTrue(eval.getComputeRange(0) == computeRanges.get(0));
		
	}
	
	@Test
	public void getComputeRangeCount() {
		
		assertTrue(eval.getComputeRangeCount() == computeRanges.size());
		
	}

}
