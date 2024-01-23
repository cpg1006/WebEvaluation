package edu.sru.WebBasedEvaluations.EvalForm;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;

import edu.sru.WebBasedEvaluations.evalform.ComputeRange;

@Suite
@SpringBootTest
public class ComputeRangeTest {
	
	static ComputeRange range;
	static String name;
	static double min;
	static double max;
	
	@BeforeAll
	static void begin() {
		
		name = "test Name";
		min = 1;
		max = 10;
		
		range = new ComputeRange(min, max, name);
		
	}
	
	@Test
	public void getRangeNameTest() {
		
		assertTrue(range.getRangeName() == "new Name");
		
	}
	
	@Test
	public void setRangeNameTest() {
		
		String newName = "new Name";
		
		range.setRangeName(newName);
		
		assertTrue(range.getRangeName() == newName);
		
	}
	
	@Test
	public void getRangeValMinTest() {
		
		assertTrue(range.getRangeValMin() == min);
		
	}
	
	@Test
	public void setRangeValMin() {
		
		double val = 4;
		
		range.setRangeValMin(val);
		
		assertTrue(range.getRangeValMin() == val);
		
	}
	
	@Test
	public void getRangeValMax() {
		
		assertTrue(range.getRangeValMax() == max);
		
	}
	
	@Test
	public void setRangeValMax() {
		
		
		double val = 4;
		
		range.setRangeValMax(val);
		
		assertTrue(range.getRangeValMax() == val);
	}

}
