package edu.sru.WebBasedEvaluations.company;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import edu.sru.WebBasedEvaluations.company.Continent;
import edu.sru.WebBasedEvaluations.company.World;

public class WorldTest {
	
	static World world; 
	static int numContinents = 4;
	static long id = 1;
	static Continent con;
	static List<Continent> childContinents;
	static String name = "test";
	
	@BeforeAll
	static public void initialize() {
		
		con = new Continent();
		
		childContinents = new ArrayList<Continent>();
		childContinents.add(con);
		
		
		world = new World(numContinents, childContinents,name);
		
		world.setId(id);
		world.setNumContinents(numContinents);
		
	}
	
	@Test
	public void addContinentTest() {
		
		Continent newCon = new Continent();
		
		assertTrue(world.addContinent(newCon));
		
	}
	
	@Test
	public void addContinentsTest() {
		
		assertTrue(world.addContinent(childContinents));
		
	}
	
	@Test
	public void removeContinentTest() {
		
		childContinents.get(0).setId(id);
		
		assertTrue(world.removeContinent(id));
		
	}
	
	@Test
	public void getIdTest() {
		
		assertTrue(world.getId() != null);
		
	}
	
	@Test
	public void setIdTest() {
		
		long newId = 2;
		
		world.setId(newId);
		
		assertTrue(world.getId() == newId);
		
	}
	
	@Test
	public void getNumContinentsTest() {
		
		assertTrue(world.getNumContinents() == numContinents);
		
	}
	
	@Test
	public void setNumContinentsTest() {
		
		int newCon = 4;
		
		world.setNumContinents(newCon);
		
		assertTrue(world.getNumContinents() == newCon);
		
	}
	
	@Test
	public void getChildContinentsTest() {
		
		assertTrue(world.getChildContinents() == childContinents);
		
	}
	
	@Test
	public void setChildContinentsTest() {
		
		childContinents.clear();
		
		world.setChildContinents(childContinents);
		
		assertTrue(world.getChildContinents() == childContinents);
		
	}
	

}
